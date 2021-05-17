package com.suse.fmall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.suse.common.utils.HttpUtils;
import com.suse.fmall.member.dao.MemberLevelDao;
import com.suse.fmall.member.entity.MemberLevelEntity;
import com.suse.fmall.member.exception.PhoneExistException;
import com.suse.fmall.member.exception.UsernameExistException;
import com.suse.fmall.member.vo.*;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.suse.common.utils.PageUtils;
import com.suse.common.utils.Query;

import com.suse.fmall.member.dao.MemberDao;
import com.suse.fmall.member.entity.MemberEntity;
import com.suse.fmall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {
    @Autowired
    private MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public void register(MemberRegisterVo vo){
        MemberEntity member = new MemberEntity();
        //检查用户名和手机号是否唯一
        checkPhoneUnique(vo.getPhone());
        checkUsernameUnique(vo.getUsername());
        member.setMobile(vo.getPhone());
        member.setUsername(vo.getUsername());
        MemberLevelEntity memberLevelEntity = memberLevelDao.getDefaultLevel();
        member.setLevelId(memberLevelEntity.getId());
        //默认昵称
        member.setNickname(vo.getUsername());
        //密码要加密存储(Spring带的)
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodePwd = passwordEncoder.encode(vo.getPassword());
        member.setPassword(encodePwd);
        baseMapper.insert(member);
    }

    @Override
    public void checkPhoneUnique(String phone) throws PhoneExistException{
        Integer count = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if(count > 0) throw new PhoneExistException();
    }

    @Override
    public void checkUsernameUnique(String username) throws UsernameExistException{
        Integer count = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", username));
        if (count > 0) throw new UsernameExistException();
    }

    /**
     * 账号密码登录
     * @param loginVo
     * @return
     */
    @Override
    public MemberEntity login(MemberLoginVo loginVo) {
        String loginacct = loginVo.getLoginacct();
        String password = loginVo.getPassword();
        MemberDao memberDao = this.baseMapper;
        MemberEntity memberEntity = memberDao.selectOne(new QueryWrapper<MemberEntity>()
                .eq("username", loginacct).or().eq("mobile", loginacct));
        if (memberEntity == null){
            //登录失败
            return null;
        }else {
            //数据库中的密码
            String pwdDb = memberEntity.getPassword();
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            //密码匹配
            boolean matches = encoder.matches(password, pwdDb);
            if (matches){
                return memberEntity;
            }else {
                return null;
            }
        }
    }

    /**
     * 社交登录或注册
     * @param socialUser
     * @return
     */
    @Override
    public MemberEntity login(SocialUser socialUser){
        //登录和注册合并逻辑
        String uid = socialUser.getUid();
        //1.判断当前社交用户是否已经登录过系统
        MemberDao memberDao = this.baseMapper;
        MemberEntity memberEntity = memberDao.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", uid));
        if(memberEntity != null){
            //该社交用户注册过
            MemberEntity memberUpdate = new MemberEntity();
            memberUpdate.setId(memberEntity.getId());
            memberUpdate.setAccessToken(socialUser.getAccess_token());
            memberUpdate.setExpiresIn(socialUser.getExpires_in());
            memberDao.updateById(memberUpdate);

            memberEntity.setAccessToken(socialUser.getAccess_token());
            memberEntity.setExpiresIn(socialUser.getExpires_in());
            return memberEntity;
        }else {
            //没有查到当前社交用户记录，注册一个用户
            MemberEntity regMember = new MemberEntity();
            try {
                //查询当前社交用户的社交账号信息(昵称、性别等)
                Map<String,String> query = new HashMap<>();
                query.put("access_token",socialUser.getAccess_token());
                query.put("uid",socialUser.getUid());
                HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get", new HashMap<>(), query);
                if (response.getStatusLine().getStatusCode() == 0){
                    //查询成功
                    String json = EntityUtils.toString(response.getEntity());
                    JSONObject jsonObject = JSON.parseObject(json);
                    String name = jsonObject.getString("name");
                    String gender = jsonObject.getString("gender");
                    regMember.setNickname(name);
                    regMember.setGender("m".equals(gender) ? 1 : 0);
                }
            }catch (Exception e){
                log.error("社交登录失败！");
            }
            regMember.setSocialUid(socialUser.getUid());
            regMember.setAccessToken(socialUser.getAccess_token());
            regMember.setExpiresIn(socialUser.getExpires_in());
            memberDao.insert(regMember);
            return regMember;
        }
    }

    @Override
    public PageUtils queryPageByCondition(MemberQuery query) {
        IPage<MemberVo> page =  this.baseMapper.getMemberByPage(new Page<>(query.getPage(),query.getLimit()),query);
        return new PageUtils(page);
    }
}