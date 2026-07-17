package com.giovani.tarefas.service;

import com.giovani.tarefas.dto.ProjectRequest;
import com.giovani.tarefas.dto.ProjectResponse;
import com.giovani.tarefas.exception.BusinessRuleException;
import com.giovani.tarefas.model.entity.Project;
import com.giovani.tarefas.model.entity.ProjectMember;
import com.giovani.tarefas.model.entity.User;
import com.giovani.tarefas.model.enums.ProjectStatus;
import com.giovani.tarefas.repository.ProjectMemberRepository;
import com.giovani.tarefas.repository.ProjectRepository;
import com.giovani.tarefas.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @InjectMocks
    private ProjectService projectService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Captor
    private ArgumentCaptor<Project> projectCaptor;

    @Captor
    private ArgumentCaptor<ProjectMember> projectMemberCaptor;

    private static final String LOGGED_USERNAME = "usuario.teste";

    @BeforeEach
    void setUp() {
        // Mock Security
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);

        Mockito.lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.lenient().when(authentication.getName()).thenReturn(LOGGED_USERNAME);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        // Clear Authentication context
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should create project, assign logged user to owner/member and return response")
    void createProjectSuccess(){
        User owner = new  User();
        owner.setId(1L);
        owner.setUsername(LOGGED_USERNAME);

        ProjectRequest request = new ProjectRequest(
                "Project Name",
                "Project Description"
        );
        Project saveProject = new Project();
        saveProject.setName(request.name());
        saveProject.setDescription(request.description());
        saveProject.setOwner(owner);
        saveProject.setStatus(ProjectStatus.ACTIVE);

        ProjectMember newMember = new ProjectMember();
        newMember.setId(12L);

        Mockito.when(userRepository.findByUsername(LOGGED_USERNAME)).thenReturn(Optional.of(owner));
        Mockito.when(projectRepository.save(Mockito.any(Project.class))).thenReturn(saveProject);
        Mockito.when(projectMemberRepository.save(Mockito.any(ProjectMember.class))).thenReturn(newMember);

        ProjectResponse response = projectService.createProject(request);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("Project Name", response.name());
        Assertions.assertEquals("Project Description", response.description());

        // Verify new saved project
        Mockito.verify(projectRepository).save(projectCaptor.capture());
        Project capturedProject = projectCaptor.getValue();
        Assertions.assertEquals("Project Name", capturedProject.getName());
        Assertions.assertEquals(owner, capturedProject.getOwner());
        Assertions.assertEquals(ProjectStatus.ACTIVE, capturedProject.getStatus());

        // Verify new member saved
        Mockito.verify(projectMemberRepository).save(projectMemberCaptor.capture());
        ProjectMember capturedMember = projectMemberCaptor.getValue();
        Assertions.assertEquals(owner, capturedMember.getUser());
        Assertions.assertEquals(saveProject, capturedMember.getProject());
    }

    @Test
    @DisplayName("Should return page of projects of the logged user")
    void findUserProjectSuccess(){
        User user = new  User();
        user.setId(1L);
        user.setUsername(LOGGED_USERNAME);

        Project foundProject = new Project();
        foundProject.setId(1L);
        foundProject.setName("Project Name");
        foundProject.setOwner(user);

        ProjectMember projectMember = new ProjectMember();
        projectMember.setId(12L);
        projectMember.setUser(user);
        projectMember.setProject(foundProject);

        Mockito.when(userRepository.findByUsername(LOGGED_USERNAME)).thenReturn(Optional.of(user));
        Mockito.when(projectMemberRepository.findByUserId(1L)).thenReturn(Optional.of(projectMember));

        Page<Project> projectPage = new PageImpl<>(List.of(foundProject));

        Mockito.when(projectRepository.findAllById(Mockito.eq(1L), Mockito.any(Pageable.class)))
                .thenReturn(projectPage);

        Page<ProjectResponse> response = projectService.findUserProjects(PageRequest.of(0,10));

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getTotalElements());

        ProjectResponse projectResponse = response.getContent().getFirst();
        Assertions.assertEquals("Project Name", projectResponse.name());
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void findUserFail(){
        Mockito.when(userRepository.findByUsername(LOGGED_USERNAME)).thenReturn(Optional.empty());

        Assertions.assertThrows(BusinessRuleException.class, () -> {
            projectService.createProject(new ProjectRequest("Project Name", "Project Description"));
        });
    }

    @Test
    @DisplayName("Should throw exception when user does not participate in any project")
    void findUserProjectsFail(){
        Assertions.assertThrows(BusinessRuleException.class, () -> {
            projectService.findUserProjects(PageRequest.of(0,10));
        });
    }

    @Test
    @DisplayName("Should return the project info when id exist")
    void findProjectByIdSuccess() {
        Project foundProject = new Project();
        foundProject.setId(1L);
        foundProject.setName("Project Name");
        foundProject.setDescription("Project Description");

        Mockito.when(projectRepository.findById(1L)).thenReturn(Optional.of(foundProject));

        ProjectResponse response = projectService.findProjectById(1L);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("Project Name", response.name());
        Assertions.assertEquals("Project Description", response.description());
    }

    @Test
    @DisplayName("Should throw exception when project does not exist")
    void findProjectByIdWhenNotExist() {
        Mockito.when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        Assertions.assertThrows(BusinessRuleException.class, () -> {
            projectService.findProjectById(99L);
        });
    }

    @Test
    @DisplayName("Should return the updated project")
    void updateProjectSuccess(){
        User user = new  User();
        user.setId(1L);
        user.setUsername(LOGGED_USERNAME);

        Project project = new Project();
        project.setId(1L);
        project.setName("Project Name");
        project.setOwner(user);
        project.setStatus(ProjectStatus.ACTIVE);

        ProjectRequest request = new ProjectRequest(
                "Second Name",
                "Project Description"
        );

        Project updateProject = new Project();
        updateProject.setId(1L);
        updateProject.setName("Project Name");
        updateProject.setOwner(user);
        updateProject.setStatus(ProjectStatus.ACTIVE);

        Mockito.when(userRepository.findByUsername(LOGGED_USERNAME)).thenReturn(Optional.of(user));
        Mockito.when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        Mockito.when(projectRepository.save(project)).thenReturn(updateProject);

        ProjectResponse response = projectService.updateProject(1L, request);

        Mockito.verify(projectRepository).save(projectCaptor.capture());
        Project capturedProject = projectCaptor.getValue();
        Assertions.assertEquals("Second Name", capturedProject.getName());
        Assertions.assertEquals(user, capturedProject.getOwner());
        Assertions.assertEquals(ProjectStatus.ACTIVE, capturedProject.getStatus());
    }

    @Test
    @DisplayName("Should throw exception and not update when user is not the owner")
    void updateUserFailWhenNotOwner(){
        User user = new  User();
        user.setId(99L);
        user.setUsername(LOGGED_USERNAME);

        User owner = new  User();
        owner.setId(1L);

        Project project = new Project();
        project.setId(1L);
        project.setName("Project Name");
        project.setOwner(owner);
        project.setStatus(ProjectStatus.ACTIVE);

        ProjectRequest request = new ProjectRequest(
                "Second Name",
                "Project Description"
        );

        Mockito.when(userRepository.findByUsername(LOGGED_USERNAME)).thenReturn(Optional.of(user));
        Mockito.when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        Assertions.assertThrows(BusinessRuleException.class, () -> {
            projectService.updateProject(1L, request);

            Mockito.verify(projectRepository, Mockito.never()).save(Mockito.any(Project.class));
        });
    }

    @Test
    @DisplayName("Should throw exception and not update when status is not active")
    void updateUserFailWhenStatusNotActive(){
        User owner = new  User();
        owner.setId(1L);
        owner.setUsername(LOGGED_USERNAME);

        Project project = new Project();
        project.setId(1L);
        project.setName("Project Name");
        project.setOwner(owner);
        project.setStatus(ProjectStatus.ARCHIVED);

        ProjectRequest request = new ProjectRequest(
                "Second Name",
                "Project Description"
        );

        Mockito.when(userRepository.findByUsername(LOGGED_USERNAME)).thenReturn(Optional.of(owner));
        Mockito.when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        Assertions.assertThrows(BusinessRuleException.class, () -> {
            projectService.updateProject(1L, request);

            Mockito.verify(projectRepository, Mockito.never()).save(Mockito.any(Project.class));
        });
    }

    @Test
    @DisplayName("Should delete the project and members when is the owner deleting")
    void deleteProjectSuccess(){
        User owner = new User();
        owner.setId(1L);
        owner.setUsername(LOGGED_USERNAME);

        Project project = new Project();
        project.setId(10L);
        project.setOwner(owner);

        Mockito.when(userRepository.findByUsername(LOGGED_USERNAME)).thenReturn(Optional.of(owner));
        Mockito.when(projectRepository.findById(10L)).thenReturn(Optional.of(project));

        projectService.deleteProject(10L);

        Mockito.verify(projectMemberRepository).deleteByProjectId(10L);
        Mockito.verify(projectRepository).delete(project);
    }

    @Test
    @DisplayName("Should throw exception and not delete when user is not the owner")
    void deleteProjectWhenUserIsNotOwner() {
        User user = new User();
        user.setId(99L);
        user.setUsername(LOGGED_USERNAME);

        User owner = new User();
        owner.setId(1L);

        Project project = new Project();
        project.setId(10L);
        project.setOwner(owner);

        Mockito.when(userRepository.findByUsername(LOGGED_USERNAME)).thenReturn(Optional.of(user));
        Mockito.when(projectRepository.findById(10L)).thenReturn(Optional.of(project));

        Assertions.assertThrows(BusinessRuleException.class, () -> {
            projectService.deleteProject(10L);
        });

        Mockito.verify(projectMemberRepository, Mockito.never()).deleteByProjectId(Mockito.anyLong());
        Mockito.verify(projectRepository, Mockito.never()).delete(Mockito.any(Project.class));
    }
}