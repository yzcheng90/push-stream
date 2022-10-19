package com.suke.axsc.web.stream.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.suke.axsc.web.stream.entity.StreamSource;
import com.suke.axsc.web.stream.service.StreamSourceService;
import com.suke.axsc.web.util.R;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 推流管理
 * @author czx
 * @email object_czx@163.com
 * @date 2022-09-30 09:57:47
 */
@RestController
@AllArgsConstructor
@RequestMapping("/stream")
public class StreamSourceController {

    private final StreamSourceService streamSourceService;

    @RequestMapping(value = {"/list","/listPage"},method = RequestMethod.POST)
    public R list(@RequestBody StreamSource params){
        //查询列表数据
        QueryWrapper<StreamSource> queryWrapper = new QueryWrapper<>();

        List<StreamSource> dataList = streamSourceService.list(queryWrapper);
        return R.ok().setData(dataList);
    }


    @RequestMapping(value = "/getInfo/{streamId}",method = RequestMethod.GET)
    public R getInfo(@PathVariable("streamId") String streamId){

        return R.ok().setData(streamSourceService.getById(streamId));
    }


    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public R save(@RequestBody StreamSource param){
        boolean flag = streamSourceService.save(param);
        return R.ok().setData(flag);
    }


    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public R update(@RequestBody StreamSource param){
        boolean flag = streamSourceService.updateById(param);
        return R.ok().setData(flag);
    }


    @RequestMapping(value = "/delete/{streamId}",method = RequestMethod.POST)
    public R delete(@PathVariable("streamId")  String streamId){
        boolean flag = streamSourceService.removeById(streamId);
        return R.ok().setData(flag);
    }

    @RequestMapping(value = "/deleteBatchList",method = RequestMethod.POST)
    public R deleteBatchList(@RequestBody List<String> stream_id){
        boolean flag = streamSourceService.removeByIds(stream_id);
        return R.ok().setData(flag);
    }

}
