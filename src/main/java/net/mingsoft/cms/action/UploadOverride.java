package net.mingsoft.cms.action;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.action.BaseFileAction;
import net.mingsoft.basic.util.BasicUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@RequestMapping("/${ms.manager.path}/file/override")
public class UploadOverride extends BaseFileAction {

    @Value("${ms.upload.denied}")
    private String uploadFileDenied;

    @Value("${ms.upload.enable-web:true}")
    private Boolean uploadEnable;

    @ApiOperation(value = "处理post请求上传文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uploadPath", value = "上传文件夹地址", required =false,paramType="query"),
            @ApiImplicitParam(name = "file", value = "文件流", required =false,paramType="query"),
            @ApiImplicitParam(name = "rename", value = "是否重命名", required =false,paramType="query",defaultValue="true"),
            @ApiImplicitParam(name = "appId", value = "上传路径是否需要拼接appId", required =false,paramType="query",defaultValue="false"),
    })
    @PostMapping("/uploadSwwx")
    @ResponseBody
    public ResultData upload(BaseFileAction.Bean bean, HttpServletRequest req, HttpServletResponse res) throws IOException {
        if(uploadEnable){
            //非法路径过滤
            if(bean.getUploadPath()!=null&&(bean.getUploadPath().contains("../")||bean.getUploadPath().contains("..\\"))){
                return ResultData.build().error(getResString("err.error", new String[]{getResString("file.path")}));
            }
            // 是否需要拼接appId
            if(bean.isAppId()){
                bean.setUploadPath(BasicUtil.getApp().getAppId()+ File.separator+ bean.getUploadPath()) ;
            }
            BaseFileAction.Config config = new BaseFileAction.Config(bean.getUploadPath(),bean.getFile(),null,false,bean.isRename());
            return this.upload(config);
        }else {
            return ResultData.build().error(getResString("insufficient.permissions"));
        }
    }
}
