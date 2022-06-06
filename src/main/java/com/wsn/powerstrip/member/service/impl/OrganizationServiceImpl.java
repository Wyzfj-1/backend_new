package com.wsn.powerstrip.member.service.impl;

import com.wsn.powerstrip.common.POJO.DTO.QueryPage;
import com.wsn.powerstrip.common.POJO.DTO.Response;
import com.wsn.powerstrip.member.DAO.OrganizationDAO;
import com.wsn.powerstrip.member.DAO.UserDAO;
import com.wsn.powerstrip.member.POJO.DO.Organization;
import com.wsn.powerstrip.member.POJO.DO.User;
import com.wsn.powerstrip.member.exception.UserException;
import com.wsn.powerstrip.member.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Author: wangzilinn@gmail.com
 * @Description:
 * @Date: Created in 7:52 PM 07/10/2020
 * @Modified By:wangzilinn@gmail.com
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrganizationServiceImpl implements OrganizationService {

    final private OrganizationDAO organizationDAO;
    final private UserDAO userDAO;


    @Override
    public Organization addOrganization(Organization organization) {
        organization.setCreateTime(new Date());
        return organizationDAO.addOrganization(organization);
    }

    @Override
    public boolean deleteOrganizationById(String id) {
        if (getNumberOfUserByOrganizationId(id) == 0) {
            return organizationDAO.deleteOrganization(id);
        } else {
            throw new UserException(400, "该组织尚存在用户,不可删除");
        }
    }

    @Override
    public boolean updateOrganizationNamById(String id, String newName) {
        return organizationDAO.updateOrganizationName(id, newName);
    }

    @Override
    public List<Organization> findAllOrganizations() {
        return organizationDAO.findAllOrganizations();
    }


    @Override
    public Response.Page<User> findUsersByOrganizationId(String organizationId, QueryPage queryPage) {
        List<User> result = userDAO.findUsersByOrganizationId(organizationId, queryPage);
        Long total = userDAO.getNumberOfUserByOrganizationId(organizationId);
        return new Response.Page<>(result, queryPage, total);
    }

    @Override
    public List<User> findUsersByOrganizationId(String organizationId) {
        return userDAO.findUsersByOrganizationId(organizationId, null);
    }

    @Override
    public Response.Page<User> findUsersByKeywordANdOrganizationId(String keyword, String organizationId, QueryPage queryPage) {
        List<User> result = userDAO.findUsersByKeywordAndOrganizationId(keyword, organizationId, queryPage);
        Long total = userDAO.getNumberOfUserByKeywordAndOrganizationId(keyword, organizationId);
        return new Response.Page<>(result, queryPage, total);
    }

    @Override
    public Long getNumberOfUserByOrganizationId(String organizationId) {
        return userDAO.getNumberOfUserByOrganizationId(organizationId);
    }

    @Override
    public Organization findOrganizationById(String organizationId) {
        return organizationDAO.findOrganizationById(organizationId);
    }


}
