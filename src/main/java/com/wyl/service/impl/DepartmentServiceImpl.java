package com.wyl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wyl.dao.DepartmentMapper;
import com.wyl.dao.UserMapper;
import com.wyl.entity.Department;
import com.wyl.entity.User;
import com.wyl.service.DepartmentService;
import com.wyl.utils.DepartmentTree;
import com.wyl.vo.query.DepartmentQueryVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {
    @Resource
    private DepartmentMapper departmentMapper;
    @Resource
    private UserMapper userMapper;
    /**
     * 查询部门列表
     *
     * @param departmentQueryVo
     * @return
     */
    @Override
    public List<Department> findDepartmentList(DepartmentQueryVo departmentQueryVo) {
        //排序
        Map<Object, Object> params = new HashMap<>();
        params.put("orderBy","orderNum");
        if (!ObjectUtils.isEmpty(departmentQueryVo.getDepartmentName())){
            params.put("departmentName",departmentQueryVo.getDepartmentName());
        }
        //查询部门列表
        List<Department> departmentList = departmentMapper.selectList(params);
        //生成部门树
        List<Department> departments = DepartmentTree.makeDepartmentTree(departmentList, 0L);
        return departments;
    }

    /**
     * 查询上级部门列表
     *
     * @return
     */

    @Override
    public List<Department> findParentDepartment() {
        //创建条件构造器对象
        //QueryWrapper<Department> queryWrapper = new QueryWrapper<>();
        //排序
        //queryWrapper.orderByAsc("order_num");
        Map<Object, Object> params = new HashMap<>();
        params.put("orderBy","order_num");
        //查询部门列表
        //List<Department> departmentList = baseMapper.selectList(queryWrapper);
        List<Department> departmentList = departmentMapper.selectList(params);
        //创建部门对象
        Department department = new Department();
        department.setId(0L);
        department.setDepartmentName("顶级部门");
        department.setPid(-1L);
        departmentList.add(department);
        //生成部门树列表
        List<Department> departmentTree = DepartmentTree.makeDepartmentTree(departmentList, -1L);
        //返回部门列表
        return departmentTree;
    }

    @Override
    public boolean check(Long id) {
        //创建条件构造器
        QueryWrapper<Department> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pid",id);
        //查询数量是否大于0
        if (baseMapper.selectCount(queryWrapper)>0){
            return true;
        }
        return false;
    }

    @Override
    public boolean hasChildrenOfDepartment(Long id) {
        //创建条件构造器
        QueryWrapper<Department> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pid",id);
        //查询数量是否大于0
        if (baseMapper.selectCount(queryWrapper)>0){
            return true;
        }
        return false;
    }

    @Override
    public boolean hasUserOfDepartment(Long id) {
        //创建条件构造器
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("department_id",id);
        //查询数量是否大于0
        if (userMapper.selectCount(queryWrapper) > 0){
            return true;
        }
        return false;
    }
}
