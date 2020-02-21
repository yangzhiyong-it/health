package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.constant.RedisConstant;
import com.itheima.dao.SetmealDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.CheckItem;
import com.itheima.pojo.Setmeal;
import com.itheima.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 体检套餐服务
 */

@Service(interfaceClass = SetmealService.class)
@Transactional
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealDao setmealDao;
    @Autowired
    private JedisPool jedisPool;

    //新增套餐，同时关联检查组
    @Override
    public void add(Setmeal setmeal, Integer[] checkgroupIds) {
        setmealDao.add(setmeal);
        Integer setmealId = setmeal.getId();//获取套餐id
        this.setSetmealAndCheckGroup(setmealId,checkgroupIds);
        //完成数据库操作后需要将图片名称保存到redis
        jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES,setmeal.getImg());
    }

    @Override
    public PageResult pageQuery(QueryPageBean queryPageBean) {
        return null;
    }

    public PageResult findPage(QueryPageBean queryPageBean) {
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();//ThreadLocal
        PageHelper.startPage(currentPage,pageSize);//分页插件，会在执行sql之前将分页关键字追加到SQL后面
        Page<CheckItem> page = setmealDao.findByCondition(queryString);
        return new PageResult(page.getTotal(),page.getResult());
    }

    //查询所有套餐
    @Override
    public List<Setmeal> findAll() {
        return setmealDao.findAll();
    }

    //查询套餐详情
    @Override
    public Setmeal findById(Integer id) {
       /* Setmeal setmeal = setmealDao.findById(id);//获取套餐基本数据
        int[] ids = {id};
        for (int setmealId : ids) {
            List<CheckGroup> checkGroups = checkGroupDao.listBySelmealId(setmealId);//根据套餐id获取所有的检查组
            for (CheckGroup group : checkGroups) {
                List<CheckItem> checkItems = checkItemDao.findCheckItemByCheckGroupId(group.getId());//根据检查组id获取所有的检查项
                group.setCheckItems(checkItems);
            }
            setmeal.setCheckGroups(checkGroups);
        }
        return setmeal;*/
        return setmealDao.findById4Detail(id);
    }

    //查询套餐占比统计数据
    @Override
    public List<Map> getSetmealReport() {
        return setmealDao.getSetmealReport();
    }

    //设置套餐和检查组多对多关联关系
    public void setSetmealAndCheckGroup(Integer setmealId,Integer[] checkgroupIds){
        if(checkgroupIds != null && checkgroupIds.length > 0){
            for (Integer checkgroupId : checkgroupIds) {
                Map<String,Integer> map = new HashMap<>();
                map.put("setmealId",setmealId);
                map.put("checkgroupId",checkgroupId);
                setmealDao.setSetmealAndCheckGroup(map);
            }
        }
    }
}
