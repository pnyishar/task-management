package oasis.task.tech.config;

import oasis.task.tech.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Author: Paul Nyishar
 * Date:12/7/24
 * Time:1:44PM
 * Project:task-management
 */
@Component
public class ApplicationInitializer implements ApplicationRunner {

    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    private PermissionService permissionService;
    private String adminUserEmail;

    private final Logger logger = LoggerFactory.getLogger(ApplicationInitializer.class);

    @Autowired
    public ApplicationInitializer(UserService userService,
                                  PermissionService permissionService,
                                  RoleService roleService,
                                  @Value("${ip.admin.userEmail}") String adminUserEmail) {
        this.userService = userService;
        this.permissionService = permissionService;
        this.roleService = roleService;
        this.adminUserEmail = adminUserEmail;
    }


    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (userService.count() == 0) initDatabaseWithDefaultData();
        logger.info("Database initialized...");
    }

    private void initDatabaseWithDefaultData() {
        createDefaultRolesAndPermissions();
        createAdminIfNotFound();
    }

    private void createDefaultRolesAndPermissions() {
        logger.info("Creating default permissions and Roles");
        this.createDefaultPermissionsIfNotFound();
        this.createDefaultRolesIfNotFound();
    }

    private void createAdminIfNotFound() {
        logger.info("Creating default user");
        //if (userService.getByEmail(adminUserTag) == null)
        userService.save(getAdminUser());
    }

    private User getAdminUser() {
        User adminUser = new User(adminUserEmail, passwordEncoder.encode("password"));
        adminUser.setFullName("Paul Nyishar");
        adminUser.setPhone("07039680088");
        adminUser.setUserType(UserType.SUPER_ADMIN);
        adminUser.activate();
        adminUser.setRoles(new HashSet<>(Collections.singletonList(roleService.findByName("SUPER_ADMIN"))));
        return adminUser;
    }

    private void createDefaultPermissionsIfNotFound() {
        logger.info("Creating default permissions");
        for (UserPermission userPermission : UserPermission.values()) {
            if (permissionService.findByName(userPermission.name()) == null) {
                permissionService.save(new Permission(userPermission.name()));
            }
        }
    }

    private void createDefaultRolesIfNotFound() {
        logger.info("Creating default roles");
        createRoleIfNotFound("SUPER_ADMIN", this.getSuperAdminPermissions());
        createRoleIfNotFound("STUDENT", this.getStudentPermissions());
    }

    private void createRoleIfNotFound(final String name, final Set<Permission> permissions) {
        if (roleService.findByName(name) == null) {
            Role role = new Role(name);
            role.setPermissions(permissions);
            roleService.save(role);
        }
    }

    private Set<Permission> getSuperAdminPermissions() {
        return new HashSet<>(permissionService.findAll());
    }

    private Set<Permission> getStudentPermissions(){
        return new HashSet<>(Arrays.asList(this.getCanViewEvents(), this.getCanGenerateInstitutionReports(),
                this.getCanProvideFeedback(), this.getCanViewUpcomingEvents(), this.getCanViewPersonalSchedule(),
        this.getCanViewNotifications()));
    }

    private Permission getCanViewEvents(){
        return permissionService.find(UserPermission.CAN_VIEW_EVENTS);
    }

    private Permission getCanGenerateInstitutionReports(){
        return permissionService.find(UserPermission.CAN_GENERATE_ALL_INSTITUTIONS_REPORTS);
    }

    private Permission getCanProvideFeedback(){
        return permissionService.find(UserPermission.CAN_PROVIDE_FEEDBACK);
    }

    private Permission getCanViewUpcomingEvents(){
        return permissionService.find(UserPermission.CAN_VIEW_UPCOMING_EVENT);
    }

    private Permission getCanViewPersonalSchedule(){
        return permissionService.find(UserPermission.CAN_VIEW_PERSONAL_SCHEDULE);
    }

    private Permission getCanViewNotifications(){
        return permissionService.find(UserPermission.CAN_VIEW_NOTIFICATIONS);
    }
}
