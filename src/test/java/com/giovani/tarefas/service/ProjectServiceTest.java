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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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
    @DisplayName("Should throw exception when user not found")
    void findUserFail(){
        Mockito.when(userRepository.findByUsername(LOGGED_USERNAME)).thenReturn(Optional.empty());

        Assertions.assertThrows(BusinessRuleException.class, () -> {
            projectService.createProject(new ProjectRequest("Project Name", "Project Description"));
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

}