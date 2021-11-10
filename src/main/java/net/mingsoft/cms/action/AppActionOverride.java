package net.mingsoft.cms.action;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.action.BaseAction;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.biz.IAppBiz;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.constant.e.CookieConstEnum;
import net.mingsoft.basic.entity.AppEntity;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.cms.biz.IAppOverrideBiz;
import net.mingsoft.cms.entity.AppEntityOverride;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/${ms.manager.path}/cms/app/")
public class AppActionOverride extends BaseAction {
    @Autowired
    private IAppBiz appBiz;

    @Autowired
    private IAppOverrideBiz appBizOverride;

    /**
     * 跳转到修改页面
     *
     * @param mode
     *            ModelMap实体对象
     * @param appId
     *            站点id
     * @param request
     *            请求对象
     * @return 站点修改页面
     */
    @ApiOperation(value = "跳转到修改页面")
    @ApiImplicitParam(name = "appId", value = "站点ID", required = true,paramType="path")
    @GetMapping(value = "/{appId}/edit")
    public String edit(ModelMap mode, @PathVariable @ApiIgnore int appId, HttpServletRequest request) {
        AppEntity app = null;
        //若有appid直接根据appId查询
        if (appId < 0) {
            app = BasicUtil.getApp();
            if(app!=null) {
                //防止session再次压入appId
                if(BasicUtil.getSession("appId")==null){
                    BasicUtil.setSession("appId",app.getAppId());
                }
            } else {
                appId = (int) BasicUtil.getSession("appId");
                app =  appBiz.getById(appId);
            }
        } else {
            app =  appBiz.getById(appId);
        }
        //查询Override部分
        AppEntityOverride override = appBizOverride.getById(app.getId());
        app.setAppUrl(override.getAppUrl());
        AppEntityOverride resData = JSON.parseObject(JSON.toJSONString(app), AppEntityOverride.class);
        if(override != null) {
            resData.setBanner(override.getBanner());
            resData.setSwwx(override.getSwwx());
            resData.setGzh(override.getGzh());
        }

        // 判断否是超级管理员,是的话不显示站点风格
        mode.addAttribute("SystemManager", true);
        mode.addAttribute("app", resData);
        mode.addAttribute("appId", appId);
        return "/basic/app/app";

    }


    @ApiOperation(value = "获取站点信息")
    @ApiImplicitParam(name = "appId", value = "站点ID", required = true,paramType="path")
    @GetMapping(value = "/{appId}/get")
    @ResponseBody
    public ResultData get(@PathVariable @ApiIgnore int appId) {
        AppEntity app = null;
        //若有appid直接根据appId查询
        if (appId < 0) {
            app = BasicUtil.getApp();
            if(app!=null) {
                //防止session再次压入appId
                if(BasicUtil.getSession("appId")==null){
                    BasicUtil.setSession("appId",app.getAppId());
                }
            } else {
                appId = (int) BasicUtil.getSession("appId");
                app =  appBiz.getById(appId);
            }
        } else {
            app =  appBiz.getById(appId);
        }
        //查询Override部分
        AppEntityOverride override = appBizOverride.getById(app.getId());
        app.setAppUrl(override.getAppUrl());
        AppEntityOverride resData = JSON.parseObject(JSON.toJSONString(app), AppEntityOverride.class);
        if(override != null) {
            resData.setBanner(override.getBanner());
            resData.setSwwx(override.getSwwx());
            resData.setGzh(override.getGzh());
        }
        return ResultData.build().success(resData);

    }

    @PostMapping("/update")
    @LogAnn(title = "更新站点信息",businessType = BusinessTypeEnum.UPDATE)
    @RequiresPermissions("app:update")
    @ResponseBody
    public ResultData update(@ModelAttribute @ApiIgnore AppEntityOverride app, ModelMap mode, HttpServletRequest request,
                             HttpServletResponse response) {
        mode.clear();
        // 获取Session值
        ManagerEntity managerSession = (ManagerEntity) getManagerBySession();
        if (managerSession == null) {
            return ResultData.build().error();
        }
        mode.addAttribute("managerSession", managerSession);

        //验证重复
        if(super.validated("app", "app_dir", app.getAppDir(), app.getId(), "id")){
            return ResultData.build().error(getResString("err.exist", this.getResString("app.dir")));
        }

        //验证网站生成目录的值是否合法
        if(StringUtil.isBlank(app.getAppDir())){
            return ResultData.build().error(getResString("err.empty", this.getResString("app.dir")));
        }
        if(!StringUtil.checkLength(app.getAppDir()+"", 0, 10)){
            return ResultData.build().error(getResString("err.length", this.getResString("app.dir"), "0", "10"));
        }

        // 判断否是超级管理员,不是则不修改应用续费时间和清单
        if (!this.isSystemManager()) {
            app.setAppPayDate(null);
            app.setAppPay(null);
        }
        int roleId = managerSession.getRoleId();
        // 判断站点数据的合法性
        // 获取cookie
        String cookie = BasicUtil.getCookie(CookieConstEnum.PAGENO_COOKIE);
        int pageNo = 1;
        // 判断cookies是否为空
        if (StringUtils.isNotBlank(cookie) && Integer.valueOf(cookie) > 0) {
            pageNo = Integer.valueOf(cookie);
        }
        mode.addAttribute("pageNo", pageNo);
        if (!checkForm(app, response)) {
            return ResultData.build().error();
        }
        if (StringUtils.isNotBlank(app.getAppLogo())) {
            app.setAppLogo(app.getAppLogo().replace("|", ""));
        }
        if (StringUtils.isNotBlank(app.getBanner())) {
            app.setBanner(app.getBanner().replace("|", ""));
        }
        if (StringUtils.isNotBlank(app.getSwwx())) {
            app.setSwwx(app.getSwwx().replace("|", ""));
        }
        if (StringUtils.isNotBlank(app.getGzh())) {
            app.setGzh(app.getGzh().replace("|", ""));
        }
//        app.setAppUrl(BasicUtil.getUrl());
        appBizOverride.updateById(app);
        appBiz.updateCache();
        return ResultData.build().success();
    }


    public boolean checkForm(AppEntityOverride app, HttpServletResponse response) {

        /*
         * 判断数据的合法性
         */
        if (!StringUtil.checkLength(app.getAppKeyword(), 0, 1000)) {
            ResultData.build().error(getResString("err.length", this.getResString("appKeyword"), "0", "1000"));
            return false;
        }
        if (!StringUtil.checkLength(app.getAppCopyright(), 0, 1000)) {
            ResultData.build().error(getResString("err.length", this.getResString("appCopyright"), "0", "1000"));
            return false;
        }
        if (!StringUtil.checkLength(app.getAppDescription(), 0, 1000)) {
            ResultData.build().error(getResString("err.length", this.getResString("appDescrip"), "0", "1000"));
            return false;
        }
        if (!StringUtil.checkLength(app.getAppName(), 1, 50)) {
            ResultData.build().error(getResString("err.length", this.getResString("appTitle"), "1", "50"));
            return false;
        }
        if (StringUtils.isNotBlank(app.getAppStyle()) && !StringUtil.checkLength(app.getAppStyle(), 1, 30)) {
            ResultData.build().error(getResString("err.length", this.getResString("appStyle"), "1", "30"));
            return false;
        }
        if(ObjectUtil.isNotNull(app.getAppHostUrl())){
            if (!StringUtil.checkLength(app.getAppHostUrl(), 10, 150)) {
                ResultData.build().error(getResString("err.length", this.getResString("appUrl"), "10", "150"));
                return false;
            }
        }
        return true;
    }
}
