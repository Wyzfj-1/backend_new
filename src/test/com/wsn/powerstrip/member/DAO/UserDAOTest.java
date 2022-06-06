package com.wsn.powerstrip.member.DAO;

import com.wsn.powerstrip.member.POJO.DO.User;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.Socket;
import java.util.List;

/**
 * @Author: hufei
 * @Modified :wangzilinn
 * @Date: 12/6/2020 9:04 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class UserDAOTest {
    @Autowired
    UserDAO userDAO;
    User user, user1;

    Socket socket = new Socket();

    @Before
    public void init(){
        //新增数据
        user = new User();
        user1 = new User();
        user.setId("test_id");
        user1.setId("test_id2");
    }

    public void add(User _user){
        userDAO.addUser(_user);
        User user1 = userDAO.findUserById(_user.getId());
        Assert.assertNotNull(user1);
    }

    public void delete(User _user){
        userDAO.deleteUserById(_user.getId());
        User user1 = userDAO.findUserById(_user.getId());
        Assert.assertEquals(null, user1);
    }

    @After
    public void destroy(){
        //结束后删除
        delete(user);
        delete(user1);
    }

    @Test
    public void testAddUser(){
        //测试新增
        add(user);
    }

    @Test
    public void testUpdateUser() {
        //测试新增
        user.setNickname("test_nickname");
        add(user);

        //测试更新
        String newNickname = "test_nickname2";
        user.setNickname(newNickname);
        userDAO.updateUser(user);
        User user2 = userDAO.findUserById(user.getId());
        Assert.assertEquals(newNickname, user2.getNickname());
    }

    @Test
    public void testUpdateUserPasswordById() {
        //测试新增
        user.setPassword("test_pwd123");
        add(user);

        //测试更新
        String newPassword = "test_pwd321";
        userDAO.updateUserPasswordById(user.getId(), newPassword);
        User user2 = userDAO.findUserById(user.getId());
        Assert.assertEquals(newPassword, user2.getPassword());
    }

    @Test
    public void testFindUserById() {
        //测试新增（包含查询）
        add(user);
    }

    @Test
    public void testFindUserByEmail() {
        //测试新增
        String email = "test@123.com";
        user.setEmail(email);
        add(user);

        //测试按邮箱查询
        User user2 = userDAO.findUserByEmail(email);
        Assert.assertNotNull(user2);
        Assert.assertEquals(user.getId(), user2.getId());
    }

    @Test
    public void testFindUserByPhone() {
        //测试新增
        String phone = "11112222333";
        user.setPhone(phone);
        add(user);

        //测试按电话查询
        User user2 = userDAO.findUserByPhone(phone);
        Assert.assertNotNull(user2);
        Assert.assertEquals(user.getId(), user2.getId());
    }

    @Test
    public void testFindUsersByUserName() {
        //测试新增
        String nickname = "test_name";
        user.setNickname(nickname);
        user1.setNickname(nickname);
        add(user);
        add(user1);

        //测试按名字查询
        User user = userDAO.findUserByNickname(nickname);
        boolean flag = false;
        if((user.getId().equals(user.getId()) && user.getId().equals(user1.getId())) ||
                (user.getId().equals(user1.getId()) && user.getId().equals(user.getId())))
            flag = true;
        Assert.assertEquals(true, flag);
    }

    @Test
    public void testFindUsersByOrganizationId() {
        //测试新增
        // String oid = "test_oid";
        // user.setOrganizationId(oid);
        // user1.setOrganizationId(oid);
        // add(user);
        // add(user1);
        //
        // //测试按组织编号查询
        // List<User> users = userDAO.findUsersByOrganizationId(oid, );
        // boolean flag = false;
        // if((users.get(0).getId().equals(user.getId()) && users.get(1).getId().equals(user1.getId())) ||
        //         (users.get(0).getId().equals(user1.getId()) && users.get(1).getId().equals(user.getId())))
        //     flag = true;
        // Assert.assertEquals(2, users.size());
        // Assert.assertEquals(true, flag);
    }

    @Test
    public void testGetNumberOfUserByOrganizationId() {
        //测试新增
        String oid = "test_oid";
        user.setOrganizationId(oid);
        user1.setId("test_id2");
        user1.setOrganizationId(oid);
        add(user);
        add(user1);

        //测试组织内用户数
        // List<User> users = userDAO.findUsersByOrganizationId(oid, );
        // Assert.assertEquals(2, users.size());
    }

    @Test
    public void testDeleteUserById() {
        //测试新增
        add(user);

        //测试删除
        delete(user);
    }
}
