package net.mingsoft.cms.dao;

import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.basic.entity.AppEntity;
import net.mingsoft.cms.entity.AppEntityOverride;
import org.springframework.stereotype.Component;

@Component
public interface IAppOverrideDao extends IBaseDao<AppEntityOverride> {
    /**
     * 获取App信息
     * @return
     */
    public AppEntityOverride getAppInfo();
}
