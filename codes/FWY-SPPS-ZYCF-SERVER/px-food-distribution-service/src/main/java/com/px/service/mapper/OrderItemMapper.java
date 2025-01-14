package com.px.service.mapper;

import java.util.List;
import com.px.service.domain.OrderItem;

/**
 * 订单明细Mapper接口
 * 
 * @author 品讯科技
 * @date 2024-08
 */
public interface OrderItemMapper 
{
    /**
     * 查询订单明细
     * 
     * @param id 订单明细ID
     * @return 订单明细
     */
    public List<OrderItem> selectOrderItemById(Long id);

    /**
     * 查询订单明细列表
     * 
     * @param orderItem 订单明细
     * @return 订单明细集合
     */
    public List<OrderItem> selectOrderItemList(OrderItem orderItem);

    /**
     * 新增订单明细
     * 
     * @param orderItem 订单明细
     * @return 结果
     */
    public int insertOrderItem(OrderItem orderItem);

    /**
     * 修改订单明细
     * 
     * @param orderItem 订单明细
     * @return 结果
     */
    public int updateOrderItem(OrderItem orderItem);

    /**
     * 删除订单明细
     * 
     * @param id 订单明细ID
     * @return 结果
     */
    public int deleteOrderItemById(Long id);

    /**
     * 批量删除订单明细
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteOrderItemByIds(Long[] ids);
}
