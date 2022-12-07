package com.hmdp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.hmdp.utils.RedisConstants;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public List<ShopType> queryShopTypeList() {
        // 1.从redis中查询商铺类型
        String shopTypeJson = stringRedisTemplate.opsForValue().get(RedisConstants.CACHE_SHOP_TYPE);

        // 2. 判断店铺是否为空
        if(!StrUtil.isBlank(shopTypeJson)){
            List<ShopType> shopTypes = JSONUtil.toList(shopTypeJson, ShopType.class);
            return shopTypes;
        }
        // 3. redis查询为空，从mysql查询
        List<ShopType> shopTypes = query().orderByAsc("sort").list();
        if(shopTypes == null){
            return null;
        }
        String shopTypeList = JSONUtil.toJsonStr(shopTypes);
        stringRedisTemplate.opsForValue().set(RedisConstants.CACHE_SHOP_TYPE,shopTypeList);
        return shopTypes;
    }
}
