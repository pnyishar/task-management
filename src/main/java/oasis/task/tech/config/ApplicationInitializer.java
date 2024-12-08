package oasis.task.tech.config;

import oasis.task.tech.constants.UserPermission;
import oasis.task.tech.constants.UserType;
import oasis.task.tech.domains.actors.User;
import oasis.task.tech.domains.security.Permission;
import oasis.task.tech.domains.security.Role;
import oasis.task.tech.service.PermissionService;
import oasis.task.tech.service.RoleService;
import oasis.task.tech.service.interfaces.UserService;
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
        createRoleIfNotFound("USER", this.getUserPermissions());
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

    private Set<Permission> getUserPermissions(){
        return new HashSet<>(Arrays.asList(this.getCanViewTask(), this.getCanCreateTask(),
                this.getCanUpdateTask(), this.getCanDeleteTask()));
    }

    private Permission getCanViewTask(){
        return permissionService.find(UserPermission.CAN_VIEW_TASK);
    }

    private Permission getCanCreateTask(){
        return permissionService.find(UserPermission.CAN_CREATE_TASK);
    }

    private Permission getCanUpdateTask(){
        return permissionService.find(UserPermission.CAN_UPDATE_TASK);
    }

    private Permission getCanDeleteTask(){
        return permissionService.find(UserPermission.CAN_DELETE_TASK);
    }

    private Permission getCanCreateUser(){
        return permissionService.find(UserPermission.CAN_CREATE_USER);
    }

    private Permission getCanUpdateUser(){
        return permissionService.find(UserPermission.CAN_UPDATE_USER);
    }
    private Permission getCanViewUsers(){
        return permissionService.find(UserPermission.CAN_VIEW_USER);
    }
}
