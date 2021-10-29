package com.luckyframe.framework.web.service;

import java.util.List;
import java.util.stream.Collectors;

import com.luckyframe.project.system.dict.domain.DictItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.luckyframe.project.system.dict.domain.DictData;
import com.luckyframe.project.system.dict.service.IDictDataService;

/**
 * ruoyi首创 html调用 thymeleaf 实现字典读取
 * 
 * @author ruoyi
 */
@Service("dict")
public class DictService
{
    @Autowired
    private IDictDataService dictDataService;

    /**
     * 根据字典类型查询字典数据信息
     * 
     * @param dictType 字典类型
     * @return 参数键值
     */
    public List<DictData> getType(String dictType)
    {
        //return dictDataService.selectDictDataByType(dictType);
        return dictDataService.selectDictDataByTypeEnabled(dictType);
    }

    /**
     * 根据字典类型查询字典数据信息
     *
     * @param dictType 字典类型
     * @return 参数键值
     */
    public List<DictItem> getItem(String dictType)
    {
        //return dictDataService.selectDictDataByType(dictType);
        return dictDataService.selectDictDataByTypeEnabled(dictType).stream().map(c-> DictItem.builder().text(c.getDictLabel()).value(c.getDictValue()).build()).collect(Collectors.toList());
    }
    /**
     * 根据字典类型和字典键值查询字典数据信息
     * 
     * @param dictType 字典类型
     * @param dictValue 字典键值
     * @return 字典标签
     */
    public String getLabel(String dictType, String dictValue)
    {
        return dictDataService.selectDictLabel(dictType, dictValue);
    }
}
