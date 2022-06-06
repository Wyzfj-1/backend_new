package com.wsn.powerstrip.member.service;

import com.wsn.powerstrip.common.POJO.DTO.QueryPage;
import com.wsn.powerstrip.common.POJO.DTO.Response;
import com.wsn.powerstrip.member.POJO.DO.Organization;
import com.wsn.powerstrip.member.POJO.DO.User;

import java.util.List;

/**
 * @Author: wangzilinn@gmail.com
 * @Description:
 * @Date: Created in 7:47 PM 07/10/2020
 * @Modified By:wangzilinn@gmail.com
 */
public interface OrganizationService {

    Organization addOrganization(Organization organization);

    boolean deleteOrganizationById(String id);

    boolean updateOrganizationNamById(String id, String newName);

    List<Organization> findAllOrganizations();

    Response.Page<User> findUsersByOrganizationId(String organizationId, QueryPage queryPage);
    List<User> findUsersByOrganizationId(String organizationId);

    Response.Page<User> findUsersByKeywordANdOrganizationId(String keyword, String organizationId, QueryPage queryPage);

    Long getNumberOfUserByOrganizationId(String organizationId);

    Organization findOrganizationById(String organizationId);
}
