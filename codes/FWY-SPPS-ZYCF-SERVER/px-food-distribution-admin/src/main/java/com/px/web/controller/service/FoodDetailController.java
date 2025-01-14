package com.px.web.controller.service;

import com.px.common.annotation.Log;
import com.px.common.core.controller.BaseController;
import com.px.common.core.domain.AjaxResult;
import com.px.common.core.page.TableDataInfo;
import com.px.common.enums.BusinessType;
import com.px.common.utils.poi.ExcelUtil;
import com.px.service.domain.FoodDetail;
import com.px.service.service.IFoodDetailService;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 食材详细信息Controller
 *
 * @author 品讯科技
 * @date 2024-08
 */
@RestController
@RequestMapping("/service/detail")
public class FoodDetailController extends BaseController {
    @Autowired
    private IFoodDetailService foodDetailService;

    /**
     * 查询食材详细信息列表
     */
    @PreAuthorize("@ss.hasPermi('service:detail:list')")
    @GetMapping("/list")
    public TableDataInfo list(FoodDetail foodDetail) {
        startPage();
        List<FoodDetail> list = foodDetailService.selectFoodDetailList(foodDetail);
        return getDataTable(list);
    }

    /**
     * 导出食材详细信息列表
     */
    @PreAuthorize("@ss.hasPermi('service:detail:export')")
    @Log(title = "食材详细信息", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(FoodDetail foodDetail) {
        List<FoodDetail> list = foodDetailService.selectFoodDetailList(foodDetail);
        ExcelUtil<FoodDetail> util = new ExcelUtil<FoodDetail>(FoodDetail.class);
        return util.exportExcel(list, "食材详细信息数据");
    }

    /**
     * 获取食材详细信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('service:detail:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(foodDetailService.selectFoodDetailById(id));
    }

    /**
     * 获取所属分类
     */
    @PreAuthorize("@ss.hasPermi('service:detail:query')")
    @GetMapping(value = "/list_options")
    public AjaxResult listOptions() {
        return AjaxResult.success(foodDetailService.selectListOptions());
    }

    /**
     * 新增食材详细信息
     */
    @PreAuthorize("@ss.hasPermi('service:detail:add')")
    @Log(title = "食材详细信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FoodDetail foodDetail) {
        foodDetail.setFoodCode(RandomStringUtils.random(9, false, true));
        return toAjax(foodDetailService.insertFoodDetail(foodDetail));
    }

    /**
     * 修改食材详细信息
     */
    @PreAuthorize("@ss.hasPermi('service:detail:edit')")
    @Log(title = "食材详细信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FoodDetail foodDetail) {
        return toAjax(foodDetailService.updateFoodDetail(foodDetail));
    }

    /**
     * 删除食材详细信息
     */
    @PreAuthorize("@ss.hasPermi('service:detail:remove')")
    @Log(title = "食材详细信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(foodDetailService.deleteFoodDetailByIds(ids));
    }
}
