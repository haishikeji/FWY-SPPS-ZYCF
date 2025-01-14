package com.px.service.service;

import java.util.List;
import java.util.Map;

import com.px.service.domain.FoodSort;

/**
 * 食材分类管理Service接口
 * 
 * @author 品讯科技
 * @date 2024-08
 */
public interface IFoodSortService 
{
    /**
     * 查询食材分类管理
     * 
     * @param id 食材分类管理ID
     * @return 食材分类管理
     */
    public FoodSort selectFoodSortById(Long id);

    /**
     * 查询食材分类管理列表
     * 
     * @param foodSort 食材分类管理
     * @return 食材分类管理集合
     */
    public List<FoodSort> selectFoodSortList(FoodSort foodSort);

    /**
     * 新增食材分类管理
     * 
     * @param foodSort 食材分类管理
     * @return 结果
     */
    public int insertFoodSort(FoodSort foodSort);

    /**
     * 修改食材分类管理
     * 
     * @param foodSort 食材分类管理
     * @return 结果
     */
    public int updateFoodSort(FoodSort foodSort);

    /**
     * 批量删除食材分类管理
     * 
     * @param ids 需要删除的食材分类管理ID
     * @return 结果
     */
    public int deleteFoodSortByIds(Long[] ids);

    /**
     * 删除食材分类管理信息
     * 
     * @param id 食材分类管理ID
     * @return 结果
     */
    public int deleteFoodSortById(Long id);

    List<Map<String, Integer>> getMenuDicts();

}
