package com.px.service.service.impl;


import java.util.List;
import com.px.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.px.service.mapper.CustomerMapper;
import com.px.service.domain.Customer;
import com.px.service.service.ICustomerService;

/**
 * 客户Service业务层处理
 * 
 * @author 品讯科技
 * @date 2024-08
 */
@Service
public class CustomerServiceImpl implements ICustomerService 
{
    @Autowired
    private CustomerMapper customerMapper;

    /**
     * 查询客户
     * 
     * @param id 客户ID
     * @return 客户
     */
    @Override
    public Customer selectCustomerById(Long id)
    {
        return customerMapper.selectCustomerById(id);
    }

    /**
     * 查询客户列表
     * 
     * @param customer 客户
     * @return 客户
     */
    @Override
    public List<Customer> selectCustomerList(Customer customer)
    {
        return customerMapper.selectCustomerList(customer);
    }

    /**
     * 新增客户
     * 
     * @param customer 客户
     * @return 结果
     */
    @Override
    public int insertCustomer(Customer customer)
    {
        customer.setCreateTime(DateUtils.getNowDate());
        return customerMapper.insertCustomer(customer);
    }

    /**
     * 修改客户
     * 
     * @param customer 客户
     * @return 结果
     */
    @Override
    public int updateCustomer(Customer customer)
    {
        customer.setUpdateTime(DateUtils.getNowDate());
        return customerMapper.updateCustomer(customer);
    }

    /**
     * 批量删除客户
     * 
     * @param ids 需要删除的客户ID
     * @return 结果
     */
    @Override
    public int deleteCustomerByIds(Long[] ids)
    {
        return customerMapper.deleteCustomerByIds(ids);
    }

    /**
     * 删除客户信息
     * 
     * @param id 客户ID
     * @return 结果
     */
    @Override
    public int deleteCustomerById(Long id)
    {
        return customerMapper.deleteCustomerById(id);
    }

    /**
     * 根据账户密码查询
     * @param account
     * @param password
     * @return
     */
    @Override
    public Customer selectCustomerByAccount(String account, String password) {
        return customerMapper.selectCustomerByAccount(account,password);
    }


}
