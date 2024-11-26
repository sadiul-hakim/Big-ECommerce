package org.shopme.admin.user;

import org.shopme.admin.role.RoleService;
import org.shopme.common.entity.User;
import org.shopme.common.pojo.ChangePasswordPojo;
import org.shopme.common.pojo.TableUrlPojo;
import org.shopme.common.util.JpaResultType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService service;
    private final RoleService roleService;

    private final TableUrlPojo pageUrl = new TableUrlPojo("/users/search", "/users",
            "/users/export-csv", "/users/create");

    private static final String PAGINATION_RESULT = "userResult";
    private static final String TABLE_URL = "tableUrl";
    private static final String PAGE = "users";
    private static final String POJO_NAME = "user";
    private static final String CREATE_PAGE = "create_user";
    private static final String UPDATING_CONDITION = "updatingUser";
    private static final String SAVED_CONDITION = "savedSuccessfully";
    private static final String SAVING_CONDITION = "savingUser";
    private static final String DELETED_CONDITION = "deletedSuccessfully";
    private static final String DELETING_CONDITION = "deletingUser";
    private static final String MESSAGE = "message";

    @GetMapping
    public ModelAndView usersPage(@RequestParam(defaultValue = "0") int page, ModelAndView model) {
        var userResult = service.findAllPaginated(page);
        model.addObject(PAGINATION_RESULT, userResult);
        model.addObject(TABLE_URL, pageUrl);

        model.setViewName(PAGE);

        return model;
    }

    @GetMapping("/search")
    public ModelAndView searchUsers(@RequestParam(defaultValue = "") String text, @RequestParam(defaultValue = "0") int page, ModelAndView model) {
        var userResult = service.searchUser(text, page);
        model.addObject(PAGINATION_RESULT, userResult);
        model.addObject(TABLE_URL, pageUrl);
        model.setViewName(PAGE);

        return model;
    }

    @GetMapping("/create")
    public ModelAndView createUsersPage(ModelAndView model) {

        var roles = roleService.findAll();
        model.addObject(POJO_NAME, new User());
        model.addObject("roles", roles);
        model.setViewName(CREATE_PAGE);
        model.addObject(UPDATING_CONDITION, false);

        return model;
    }

    @PostMapping("/save")
    public ModelAndView save(
            @ModelAttribute User user,
            @RequestParam boolean updating,
            @RequestParam MultipartFile file,
            ModelAndView model
    ) {

        var result = updating ? service.updateUser(user, file) : service.save(user, file);
        model.addObject(SAVED_CONDITION, result.type().equals(JpaResultType.SUCCESSFUL));
        model.addObject(SAVING_CONDITION, true);
        model.addObject(MESSAGE, result.message());
        model.addObject(TABLE_URL, pageUrl);

        if (result.type().equals(JpaResultType.NOT_UNIQUE)) {
            var roles = roleService.findAll();
            model.addObject(POJO_NAME, user);
            model.addObject("roles", roles);
            model.setViewName(CREATE_PAGE);
        } else {
            var userResult = service.findAllPaginated(0);
            model.addObject(PAGINATION_RESULT, userResult);
            model.setViewName(PAGE);
        }
        return model;
    }

    @GetMapping("/update_page/{userId}")
    public ModelAndView updatePage(@PathVariable int userId, ModelAndView model) {

        var user = service.findById(userId);
        if (user.isEmpty()) {
            var users = service.findAllPaginated(0);
            model.addObject(TABLE_URL, pageUrl);
            model.addObject(PAGINATION_RESULT, users);
            model.setViewName(PAGE);

            model.addObject(UPDATING_CONDITION, true);
            model.addObject(MESSAGE, "User does not exists!");
            return model;
        }

        var roles = roleService.findAll();
        model.addObject(POJO_NAME, user.get());
        model.addObject("roles", roles);
        model.setViewName(CREATE_PAGE);
        model.addObject(UPDATING_CONDITION, true);

        return model;
    }

    @PostMapping("/updatePassword")
    public ModelAndView updatePassword(@ModelAttribute ChangePasswordPojo pojo, @RequestParam int userId,
                                       ModelAndView model) {
        var result = service.updatePassword(pojo, userId);
        model.addObject("changedSuccessfully", result.type().equals(JpaResultType.SUCCESSFUL));
        model.addObject("changingPassword", true);
        model.addObject(MESSAGE, result.message());
        model.addObject("pojo", result.type().equals(JpaResultType.SUCCESSFUL) ? new ChangePasswordPojo() : pojo);
        model.setViewName("changePassword");
        model.addObject("userId", userId);
        return model;
    }

    @GetMapping("/changePassword/{userId}")
    public ModelAndView changePasswordPage(@PathVariable int userId, ModelAndView model) {
        var userOptional = service.findById(userId);
        if (userOptional.isEmpty()) {
            var users = service.findAllPaginated(0);
            model.addObject(TABLE_URL, pageUrl);
            model.addObject(PAGINATION_RESULT, users);
            model.setViewName(PAGE);

            model.addObject(UPDATING_CONDITION, true);
            model.addObject(MESSAGE, "User does not exists!");
            return model;
        }

        model.addObject("userId", userId);
        model.setViewName("changePassword");
        model.addObject("pojo", new ChangePasswordPojo());

        return model;
    }

    @GetMapping("/delete/{id}")
    public ModelAndView deleteUser(@PathVariable int id, ModelAndView model) {
        var result = service.delete(id);
        model.addObject(DELETED_CONDITION, result.type().equals(JpaResultType.SUCCESSFUL));
        model.addObject(DELETING_CONDITION, true);
        model.addObject(TABLE_URL, pageUrl);
        model.addObject(MESSAGE, result.message());

        var users = service.findAllPaginated(0);
        model.addObject(PAGINATION_RESULT, users);
        model.setViewName(PAGE);
        return model;
    }

    @GetMapping("/export-csv")
    public ResponseEntity<byte[]> exportCsv() {
        var data = service.csvData();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.csv");
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }
}
