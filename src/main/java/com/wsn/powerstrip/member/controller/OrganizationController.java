package com.wsn.powerstrip.member.controller;

import com.wsn.powerstrip.common.POJO.DTO.QueryPage;
import com.wsn.powerstrip.common.POJO.DTO.Response;
import com.wsn.powerstrip.common.annotation.WebLog;
import com.wsn.powerstrip.member.POJO.DO.Organization;
import com.wsn.powerstrip.member.exception.UserException;
import com.wsn.powerstrip.member.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @Author: wangzilinn@gmail.com
 * @Description:
 * @Date: Created in 8:04 PM 07/10/2020
 * @Modified By:wangzilinn@gmail.com
 */
@RestController
@RequestMapping("/api/organization")
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Validated
@Tag(name = "Organization", description = "组织管理接口,获得一个组织中的数据,如组织所有用户列表")
public class OrganizationController {

    final private OrganizationService organizationService;

    @PostMapping
    @Operation(summary = "新建一个组织,这里只有组织名是必须的")
    public Response<?> addOrganization(@Parameter(description = "组织实体")
                                       @Valid @NotNull @RequestBody Organization organization) {
        if (organization.getId() != null) {
            throw new UserException(400, "User实体不应该有Id, 应为数据库分配" + organization.toString());
        }
        Organization addedOrganization = organizationService.addOrganization(organization);
        return new Response<>(addedOrganization);
    }

    @DeleteMapping
    @Operation(summary = "删除一个组织")
    public Response<?> deleteOrganization(@Parameter(description = "组织id")
                                          @Length(min = 24, max = 24, message = "organization Id 长度为24")
                                          @RequestParam(value = "organizationId") String organizationId) {
        if (organizationService.deleteOrganizationById(organizationId)) {
            return new Response<>("删除成功");
        } else {
            throw new UserException(500, "删除失败");
        }
    }

    @PutMapping
    @Operation(summary = "修改组织名称")
    public Response<?> updateName(@Parameter(description = "组织id")
                                  @Length(min = 24, max = 24, message = "organization Id 长度为24")
                                  @RequestParam(value = "organizationId") String organizationId,
                                  @Parameter(description = "该组织的新名称")
                                  @Length(min = 1, max = 100, message = "organization I名称在1~100之间")
                                  @RequestParam(value = "newName") String newName) {
        if (organizationService.updateOrganizationNamById(organizationId, newName)) {
            return new Response<>("更新名字成功");
        } else {
            throw new UserException(500, "更新失败");
        }
    }

    @GetMapping
    @Operation(
            summary = "根据组织id获得组织"
    )
    public Response<?> getDetail(@Parameter(description = "组织id")
                                 @Length(min = 24, max = 24, message = "organization Id 长度为24")
                                 @RequestParam(value = "organizationId") String organizationId
    ) {
        return new Response<>(organizationService.findOrganizationById(organizationId));
    }


    @GetMapping("/list")
    @Operation(summary = "获得所有组织列表")
    public Response<?> getList() {
        return new Response<>(organizationService.findAllOrganizations());
    }

    @GetMapping("/users")
    @Operation(summary = "获得这个组织中所有的用户的数量或列表, organizationId是必填项, 如果justNumber=true,则只返回该组织的用户数量,且其他参数无用,否则查询的页数是必填项")
    @WebLog
    public Response<?> getUserList(@Parameter(description = "organization id")
                                   @Length(min = 24, max = 24, message = "organization Id 长度为24")
                                   @RequestParam(value = "organizationId") String organizationId,
                                   @Parameter(description = "模糊查询用户名")
                                   @RequestParam(value = "keyword", required = false) String keyword,
                                   @Parameter(description = "查询的页数")
                                   @Range(min = 1)
                                   @RequestParam(value = "page", required = false) Integer page,
                                   @Parameter(description = "每页的长度")
                                   @Range(min = 1)
                                   @RequestParam(value = "limit", defaultValue = "10") int limit,
                                   @Parameter(description = "是否只返回用户数量")
                                   @RequestParam(value = "justNumber", required = false) Boolean justNumber) {
        if (justNumber == null || !justNumber) {
            if (page == null) {
                throw new UserException(400, "mast have page param when justNumber is not specified");
            }
            if (keyword != null) {
                return new Response<>(organizationService.findUsersByKeywordANdOrganizationId(keyword,
                        organizationId, new QueryPage(page, limit)));
            } else {
                return new Response<>(organizationService.findUsersByOrganizationId(organizationId, new QueryPage(page,
                        limit)));
            }
        } else {
            Long userNumberByOrganizationId = organizationService.getNumberOfUserByOrganizationId(organizationId);
            return new Response<>(userNumberByOrganizationId);
        }
    }


}
