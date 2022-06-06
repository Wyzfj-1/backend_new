package com.wsn.powerstrip.member.DAO;

import com.mongodb.DuplicateKeyException;
import com.wsn.powerstrip.common.POJO.DTO.QueryPage;
import com.wsn.powerstrip.member.POJO.DO.User;
import com.wsn.powerstrip.member.exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: wangzilinn@gmail.com
 * @Description:
 * @Date: Created in 12:40 AM 6/16/2020
 * @Modified By:xianyi_zhou@163.com
 */
@Repository
@Slf4j
public class UserDAO {

    @Resource
    private MongoTemplate mongoTemplateForUser;
    final private String USER_COLLECTION = "user";

    public User addUser(User user) {
        try {
            return mongoTemplateForUser.insert(user, USER_COLLECTION);
        } catch (DuplicateKeyException e) {
            throw new UserException(400, "新建用户失败, 主键已存在");
        }
    }

    public User updateUser(User user) {
        return mongoTemplateForUser.save(user, USER_COLLECTION);
    }



    public boolean updateUserPasswordById(String id, String newPassword) {
        return mongoTemplateForUser.updateFirst(
                new Query(Criteria.where("_id").is(id)),
                new Update().set("password", newPassword),
                USER_COLLECTION
        ).wasAcknowledged();
    }

    public @Nullable User findUserById(String userId) {
        Query query = new Query(Criteria.where("_id").is(userId));
        return mongoTemplateForUser.findOne(query, User.class, USER_COLLECTION);
    }

    public User findUserByEmail(String userEmail) {
        Query query = new Query(Criteria.where("email").is(userEmail));
        return mongoTemplateForUser.findOne(query, User.class, USER_COLLECTION);
    }

    public User findUserByPhone(String phone) {
        Query query = new Query(Criteria.where("phone").is(phone));
        return mongoTemplateForUser.findOne(query, User.class, USER_COLLECTION);
    }
    public User findUserByNickname(String nickname) {
        Query query = new Query(Criteria.where("nickname").is(nickname));
        return mongoTemplateForUser.findOne(query, User.class, USER_COLLECTION);
    }

    public List<User> findUsersByOrganizationId(String organizationId,@Nullable QueryPage queryPage) {
        Query query = new Query(Criteria.where("organizationId").is(organizationId));
        if (queryPage != null) {
            PageRequest pageRequest = PageRequest.of(queryPage.getPageForMongoDB(), queryPage.getLimit());
            query = query.with(pageRequest);
        }
        return mongoTemplateForUser.find(query, User.class, USER_COLLECTION);
    }

    public List<User> findUsersByRole(String role,@Nullable QueryPage queryPage) {
        Query query = new Query(Criteria.where("role").is(role));
        if (queryPage != null) {
            PageRequest pageRequest = PageRequest.of(queryPage.getPageForMongoDB(), queryPage.getLimit());
            query = query.with(pageRequest);
        }
        return mongoTemplateForUser.find(query, User.class, USER_COLLECTION);
    }

    public Long getNumberOfUserByOrganizationId(String organizationId) {
        Query query = new Query(Criteria.where("organizationId").is(organizationId));
        return mongoTemplateForUser.count(query, User.class, USER_COLLECTION);
    }

    public Long getNumberOfUserByRole(String role) {
        Query query = new Query(Criteria.where("role").is(role));
        return mongoTemplateForUser.count(query, User.class, USER_COLLECTION);
    }

    public boolean deleteUserById(String userId) {
        return mongoTemplateForUser.remove(new Query(Criteria.where("_id").is(userId)), User.class, USER_COLLECTION).wasAcknowledged();
    }

    public boolean deleteUserByEmail(String userEmail){
        return mongoTemplateForUser.remove(new Query(Criteria.where("email").is(userEmail)), User.class, USER_COLLECTION).wasAcknowledged();
    }

    public List<User> findUsersByKeywordAndOrganizationId(String keyword, String organizationId, @Nullable QueryPage queryPage) {
        keyword = String.format("^.*%s.*$", keyword);//^匹配开头, $匹配结尾, 整体含义为中间包含指定值
        Query query = new Query(Criteria.where("organizationId").is(organizationId).and("nickname").regex(keyword));
        if (queryPage != null) {
            PageRequest pageRequest = PageRequest.of(queryPage.getPageForMongoDB(), queryPage.getLimit());
            query = query.with(pageRequest);
        }
        return mongoTemplateForUser.find(query, User.class, USER_COLLECTION);
    }

    public Long getNumberOfUserByKeywordAndOrganizationId(String keyword, String organizationId) {
        keyword = String.format("^.*%s.*$", keyword);//^匹配开头, $匹配结尾, 整体含义为中间包含指定值
        Query query = new Query(Criteria.where("organizationId").is(organizationId).and("nickname").regex(keyword));
        return mongoTemplateForUser.count(query, User.class, USER_COLLECTION);
    }
    public Long getNumberOfUserByKeywordAndRole(String keyword, String role) {
        keyword = String.format("^.*%s.*$", keyword);//^匹配开头, $匹配结尾, 整体含义为中间包含指定值
        Query query = new Query(Criteria.where("role").is(role).and("nickname").regex(keyword));
        return mongoTemplateForUser.count(query, User.class, USER_COLLECTION);
    }
    public List<User> findUsersByKeywordAndRole(String keyword, String role, @Nullable QueryPage queryPage) {
        keyword = String.format("^.*%s.*$", keyword);//^匹配开头, $匹配结尾, 整体含义为中间包含指定值
        Query query = new Query(Criteria.where("role").is(role).and("nickname").regex(keyword));
        if (queryPage != null) {
            PageRequest pageRequest = PageRequest.of(queryPage.getPageForMongoDB(), queryPage.getLimit());
            query = query.with(pageRequest);
        }
        return mongoTemplateForUser.find(query, User.class, USER_COLLECTION);
    }
}
