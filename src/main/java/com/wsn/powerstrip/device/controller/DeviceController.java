package com.wsn.powerstrip.device.controller;

import com.wsn.powerstrip.common.POJO.DTO.QueryPage;
import com.wsn.powerstrip.common.POJO.DTO.Response;
import com.wsn.powerstrip.common.annotation.WebLog;
import com.wsn.powerstrip.device.POJO.DO.device.impl.Sensor;
import com.wsn.powerstrip.device.POJO.DO.deviceHistory.impl.SensorHistory;
import com.wsn.powerstrip.device.constants.DeviceStateEnum;
import com.wsn.powerstrip.device.constants.ScreenEnum;
import com.wsn.powerstrip.device.constants.SensorType;
import com.wsn.powerstrip.device.exception.DeviceDAOException;
import com.wsn.powerstrip.device.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/api/device")
@Slf4j
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Tag(name = "Device", description = "设备管理接口,CRUD,打开关闭,历史记录等等")
public class DeviceController {

    final private DeviceService deviceService;
    // final private BufferHistoryHandler bufferHistoryHandler;


    @PostMapping("/sensor")
    @WebLog(description = "添加或更新一个传感器")
    @Operation(summary = "添加/更新传感器",
            description = "添加时,name,设备类型为必填项, 更新时,传入的body可以只包含需要更新的字段和id")
    public Response<?> addOrUpdateSensor(@NotNull @RequestBody Sensor sensor) {
        //检查必要参数
        Sensor addedSensor = deviceService.addOrUpdateDevice(sensor);
        if (addedSensor != null) {
            return new Response<>(addedSensor);
        } else {
            throw new DeviceDAOException(403, "新建传感器设备失败");
        }
    }


    @DeleteMapping
    @WebLog(description = "删除一个设备")
    @Operation(summary = "根据Id删除设备")
    @CrossOrigin
    public Response<?> delete(@Parameter(description = "传感器存在数据库中的id", example = "5eec414b04f7aa392228efa1")
                              @Length(min = 24, max = 24, message = "设备id 长度为24")
                              @RequestParam("id") String id) {
        deviceService.deleteDevice(id);
        return new Response<>("已删除");
    }

    @DeleteMapping("/deleteList")
    @WebLog(description = "删除多个设备")
    @Operation(summary = "根据id进行批量设备的删除")
    public Response<?> deleteList(@Parameter(description = "id列表")
                                  @RequestBody List<String> idList) {
        deviceService.deleteDeviceList(idList);
        return new Response<>("已删除");
    }


    @GetMapping("/list")
    @WebLog
    @Operation(
            summary = "获得所有处于正常状态的传感器设备实体"
    )
    public Response<?> detail(@Parameter(description = "设备的状态")
                              @RequestParam(value = "state")DeviceStateEnum deviceState,
                              @Parameter(description = "查询的页数")
                              @Range(min = 1)
                              @RequestParam(value = "page") int page,
                              @Parameter(description = "每页的长度")
                              @Range(min = 1)
                              @RequestParam(value = "limit", defaultValue = "10") int limit
        ) {
        return new Response<>(deviceService.findAllDevicesByDeviceState(deviceState, new QueryPage(page, limit)));
    }

    @GetMapping("/summary")
    @Operation(
            summary = "获得所有设备的概览信息",
            description = "全部设备，正常设备，异常设备，报废设备，烟雾传感器，温度传感器，漏电流传感器，电表"
    )
    public Response<?> getList() {
        return new Response<>(deviceService.getSummary());
    }


    @GetMapping("/findNow")
    @WebLog
    @Operation(summary = "条件查询设备",
            description = "获取当前设备列表中的设备实体 " +
                    "key或id或sensorType或location仅可选一项(也可不选)," +
                    "如果添加了多项则以第一个为准, 如果仅需要一页结果,则仅需要把limit设置大一些即可")
    public Response<?> userPowerStrip(@Parameter(description = "传感器类型")
                                      @RequestParam(value = "type", required = false) SensorType sensorType,
                                      @Parameter(description = "查询的关键字")
                                      @RequestParam(value = "keyword", required = false) String keyword,
                                      @Parameter(description = "查询的开始时间")
                                      @RequestParam(value = "startTime", required = false) Date startTime,
                                      @Parameter(description = "查询的结束时间")
                                      @RequestParam(value = "endTime", required = false) Date endTime,
                                      @Parameter(description = "查询的页数")
                                      @Range(min = 1)
                                      @RequestParam(value = "page") int page,
                                      @Parameter(description = "每页的长度")
                                      @Range(min = 1)
                                      @RequestParam(value = "limit", defaultValue = "10") int limit


    ) {
        if (keyword != null) {
            return new Response<>(deviceService.findDevicesByKey(keyword, DeviceStateEnum.NORMAL, new QueryPage(page, limit)));
        } else if (startTime != null) {
            if(endTime != null) {
                return new Response<>(deviceService.findDevicesByStartTime(startTime, endTime, DeviceStateEnum.NORMAL, new QueryPage(page, limit)));
            } else {
                return new Response<>(deviceService.findDevicesByStartTime(startTime, DeviceStateEnum.NORMAL, new QueryPage(page, limit)));
            }
        }
        return new Response<>(deviceService.findDevicesBySensorType(sensorType, DeviceStateEnum.NORMAL, new QueryPage(page, limit)));
    }

    @GetMapping("/findOne")
    @WebLog
    @Operation(description = "根据设备id查找设备实体")
    public Response<?> getOneById(@Parameter(description = "查询id")
                                  @RequestParam(value = "id", required = false) String id) {
        return new Response<>(deviceService.findDevice(id));
    }

    @GetMapping("/position")
    @WebLog
    @Operation(description = "根据位置信息查询所有的设备,包括处于异常或者报废的设备")
    public Response<?> getPositionDevice(@Parameter(description = "根据传感器位置一号大屏、二号大屏、三号大屏或者其他查询")
                                         @RequestParam(value = "position") int position,
                                         @Parameter(description = "根据传感器在大屏上的横坐标查询")
                                         @RequestParam(value = "X", required = false) Integer X,
                                         @Parameter(description = "根据传感器在大屏上的纵坐标查询")
                                         @RequestParam(value = "Y", required = false) Integer Y,
                                         @Parameter(description = "查询的页数")
                                         @Range(min = 1)
                                         @RequestParam(value = "page") int page,
                                         @Parameter(description = "每页的长度")
                                         @Range(min = 1)
                                         @RequestParam(value = "limit", defaultValue = "10") int limit) {
        if(X != null && Y != null) {
            return new Response<>(deviceService.findDevicesByPosition(position, X, Y));
        }
        return new Response<>(deviceService.findDevicesByPosition(position, new QueryPage(page, limit)));
    }


    @GetMapping("/history/{screen}")
    @WebLog
    @Operation(summary = "根据id获得历史记录", description = "返回最近(period * times)时间长度的设备历史记录")
    public Response<?> getHistory(@Parameter(description = "大屏号")
                                  @PathVariable("screen") Integer screen,
                                  @Parameter(description = "传感器id")
                                  @RequestParam(value = "id") String id,
                                  /*@Parameter(description = "设备类型", example = "TEMPERATURE")
                                  @RequestParam("type") SensorType sensorType,*/
                                  @Parameter(description = "查询的周期,参数可选:\"minute\",\"hour\", \"day\"")
                                  @RequestParam(value = "period") String period,
                                  @Parameter(description = "查询几个周期, 若无此参数则默认为1")
                                  @RequestParam(value = "times", defaultValue = "1", required = false) Integer times) {
        if (period.equals("hour") || period.equals("day") || period.equals("minute")) {
            List<SensorHistory> deviceHistoryList = deviceService.getHistory(ScreenEnum.select(screen), id, period, times);
            return new Response<>(deviceHistoryList);
        } else throw new DeviceDAOException(400, "period参数错误,输入参数为 period=" + period + " times=" + times);
    }

    // 根据目前项目设计，最新设备历史总是在mongodb中
    @GetMapping("/status/{screen}")
    @WebLog
    @Operation(summary = "根据大屏号获得所有最新温度", description = "返回最新的大屏状态")
    public Response<?> getAllLatestHistoryByScreen(@Parameter(description = "几号大屏")
                                  @PathVariable("screen") Integer screen) {
        Float[][] data = deviceService.getAllLatestHistoryByScreen(screen);
        return new Response<>(data);
    }

    // 大屏→设备查询24h历史记录
    @GetMapping("/history/oneday/{screen}")
    @WebLog
    @Operation(summary = "根据大屏和id获得历史记录", description = "返回最近24h的设备历史记录")
    public Response<?> getHistoryFor24h(@Parameter(description = "大屏号")
                                  @PathVariable("screen") Integer screen,
                                  @Parameter(description = "传感器id")
                                  @RequestParam(value = "id") String id) {
        List<SensorHistory> deviceHistoryList = deviceService.getHistory(ScreenEnum.select(screen), id, "hour", 24);
        return new Response<>(deviceHistoryList);
    }
}
