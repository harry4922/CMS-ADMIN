package net.mingsoft.cms.biz.impl;

import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.cms.biz.IAppOverrideBiz;
import net.mingsoft.cms.dao.IAppOverrideDao;
import net.mingsoft.cms.dao.ICategoryDao;
import net.mingsoft.cms.entity.AppEntityOverride;
import net.mingsoft.cms.entity.CategoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = RuntimeException.class)
public class AppBizOverrideImpl extends BaseBizImpl<IAppOverrideDao, AppEntityOverride> implements IAppOverrideBiz {

    @Autowired
    private IAppOverrideDao appOverrideDao;

    @Override
    protected IBaseDao<AppEntityOverride> getDao() {
        return appOverrideDao;
    }
}
