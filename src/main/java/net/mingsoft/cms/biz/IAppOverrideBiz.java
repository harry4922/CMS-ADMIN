package net.mingsoft.cms.biz;

import net.mingsoft.base.biz.IBaseBiz;
import net.mingsoft.basic.entity.AppEntity;
import net.mingsoft.cms.entity.AppEntityOverride;

public interface IAppOverrideBiz extends IBaseBiz<AppEntityOverride> {
    /**
     * 获取App信息
     * @return
     */
    public AppEntityOverride getAppInfo();
}
