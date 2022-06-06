package com.wsn.powerstrip.member.DAO;

import com.mongodb.DuplicateKeyException;
import com.wsn.powerstrip.member.POJO.DO.Organization;
import com.wsn.powerstrip.member.exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: wangzilinn@gmail.com
 * @Description:
 * @Date: Created in 7:32 PM 07/10/2020
 * @Modified By:wangzilinn@gmail.com
 */
@Repository
@Slf4j
public class OrganizationDAO {
    @Resource
    private MongoTemplate mongoTemplateForUser;
    final private String ORGANIZATION_COLLECTION = "organization";

    public Organization addOrganization(Organization organization) {
        try {
            return mongoTemplateForUser.insert(organization, ORGANIZATION_COLLECTION);
        } catch (DuplicateKeyException e) {
            throw new UserException(400, "新建用户失败, 主键已存在");
        }
    }

    public boolean deleteOrganization(String id) {
        return mongoTemplateForUser.remove(new Query(Criteria.where("_id").is(id)), Organization.class).wasAcknowledged();
    }



    public boolean updateOrganizationName(String id, String newName) {
        return mongoTemplateForUser.updateFirst(new Query(Criteria.where("_id").is(id)), new Update().set("name", newName),
                ORGANIZATION_COLLECTION).wasAcknowledged();
    }

    public List<Organization> findAllOrganizations() {
        return mongoTemplateForUser.findAll(Organization.class, ORGANIZATION_COLLECTION);
    }

    public Organization findOrganizationById(String organizationId) {
        return mongoTemplateForUser.findOne(new Query(Criteria.where("_id").is(organizationId)), Organization.class,
                ORGANIZATION_COLLECTION);
    }
}
