package com.egoo.idp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.egoo.idp.entity.dto.HistorySessionInfoDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HistorySessionMapper extends BaseMapper<HistorySessionInfoDto> {

}
