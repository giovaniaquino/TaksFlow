package com.giovani.tarefas.service;

import com.giovani.tarefas.dto.ProjectMemberResponse;
import com.giovani.tarefas.exception.BusinessRuleException;
import com.giovani.tarefas.model.entity.Project;
import com.giovani.tarefas.model.entity.ProjectMember;
import com.giovani.tarefas.model.entity.User;
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

@ExtendWith(MockitoExtension.class)
class ProjectMemberServiceTest {

    @InjectMocks
    private ProjectMemberService projectMemberService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectMemberRepository projectMemberRepository;

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
    @DisplayName("Should return user name and project name")
    void addUserToProjectSuccess(){
        User owner = new User();
        owner.setId(1L);
        owner.setUsername(LOGGED_USERNAME);

        User user = new User();
        user.setId(90L);
        user.setUsername("User");

        Project project = new Project();
        project.setId(10L);
        project.setOwner(owner);
        project.setName("Project");

        ProjectMember projectMember = new ProjectMember();
        projectMember.setId(12L);
        projectMember.setUser(user);
        projectMember.setProject(project);

        Mockito.when(projectRepository.findById(10L)).thenReturn(Optional.of(project));
        Mockito.when(userRepository.findById(90L)).thenReturn(Optional.of(user));
        Mockito.when(projectMemberRepository.save(Mockito.any(ProjectMember.class))).thenReturn(projectMember);

        ProjectMemberResponse response = projectMemberService.addUserToProject(10L, 90L);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("User", response.username());
        Assertions.assertEquals("Project", response.projectName());

        //Verify saved member
        Mockito.verify(projectMemberRepository).save(projectMemberCaptor.capture());
        ProjectMember captured =  projectMemberCaptor.getValue();
        Assertions.assertNotNull(captured);
        Assertions.assertEquals("User", captured.getUser().getUsername());
        Assertions.assertEquals("Project", captured.getProject().getName());
    }

    @Test
    @DisplayName("Should throw exception when non owner try to add to project")
    void notOwnerTryAddFail(){
        User owner = new  User();
        owner.setId(1L);
        owner.setUsername("Owner");

        Project project = new  Project();
        project.setId(10L);
        project.setName("Project");
        project.setOwner(owner);

        Mockito.when(projectRepository.findById(10L)).thenReturn(Optional.of(project));

        Assertions.assertThrows(BusinessRuleException.class, () -> {
            projectMemberService.addUserToProject(10L, 2L);
        });
        Mockito.verify(projectMemberRepository, Mockito.never()).save(Mockito.any(ProjectMember.class));
    }

    @Test
    @DisplayName("Should delete user from project")
    void deleteUserFromProjectSuccess(){
        User owner = new User();
        owner.setId(1L);
        owner.setUsername(LOGGED_USERNAME);

        User user = new User();
        user.setId(2L);

        Project project = new Project();
        project.setId(10L);
        project.setOwner(owner);

        Mockito.when(projectRepository.findById(10L)).thenReturn(Optional.of(project));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        projectMemberService.deleteUserFromProject(10L, 2L);

        Mockito.verify(projectMemberRepository).deleteByProjectIdAndUserId(10L, 2L);
    }

    @Test
    @DisplayName("Should throw exception when non owner try to delete to project")
    void notOwnerTryDeleteFail(){
        User owner = new  User();
        owner.setId(1L);
        owner.setUsername("Owner");

        Project project = new  Project();
        project.setId(10L);
        project.setName("Project");
        project.setOwner(owner);

        Mockito.when(projectRepository.findById(10L)).thenReturn(Optional.of(project));

        Assertions.assertThrows(BusinessRuleException.class, () -> {
            projectMemberService.deleteUserFromProject(10L, 2L);
        });
        Mockito.verify(projectMemberRepository, Mockito.never()).save(Mockito.any(ProjectMember.class));
    }
}