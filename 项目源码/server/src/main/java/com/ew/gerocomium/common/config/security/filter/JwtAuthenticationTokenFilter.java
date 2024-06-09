package com.ew.gerocomium.common.config.security.filter;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.ew.gerocomium.common.constant.Constant;
import com.ew.gerocomium.common.constant.ExceptionEnum;
import com.ew.gerocomium.common.util.*;
import com.ew.gerocomium.dao.vo.LoginUserVo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private CommonUtil commonUtil;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 获取jwt
        String jwt = request.getHeader("token");
        // 获取请求路径
        String requestURI = request.getRequestURI();
        // 验证url是否在白名单之内S
        boolean ignore = commonUtil.existFlag(commonUtil.getIgnoreUrlArray(), requestURI);
        // jwt为空 且 url在白名单之内
        if (!StringUtils.hasText(jwt) && ignore) {
            filterChain.doFilter(request, response);
            return;
        }
        // （jwt为空 但 url未在白名单之内） 或 （jwt不为空 且 url在白名单之内）
        boolean accessError = (!StringUtils.hasText(jwt) && !ignore) || (StringUtils.hasText(jwt) && ignore);
        if (accessError) {
            ResponseUtil.exceptionEnumOut(response, ExceptionEnum.ACCESS_ERROR);
            return;
        }
        // jwt不为空 且 url未在白名单之内
        // 解析jwt获取用户id
        String id;
        try {
            Jws<Claims> claimsJws = JwtUtil.parseJwt(jwt);
            id = claimsJws.getBody().get("id").toString();
        } catch (Exception e) {
            e.printStackTrace();
            // token解析出错（非法token/token过期）
            ResponseUtil.exceptionEnumOut(response, ExceptionEnum.TOKEN_ERROR);
            return;
        }
        // 从redis获取用户信息
        LoginUserVo loginUserVo = BeanUtil.toBean(redisUtil.getCacheObject(Constant.LOGIN_REDIS + id), LoginUserVo.class);
        // 获取redis数据为空
        if (loginUserVo == null) {
            ResponseUtil.exceptionEnumOut(response, ExceptionEnum.TOKEN_ERROR);
            return;
        }
        // 打印访问日志
        log.info("用户：{}，访问：{}，成功！", loginUserVo.getId(), request.getRequestURL().toString());
        // TODO 存入SecurityContextHolder上下文
        // 获取权限列表并转换
        List<SimpleGrantedAuthority> authorityList = loginUserVo.getAuthUrlList().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        // 认证
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginUserVo, null, authorityList);
        // 存入上下文
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        // 放行
        filterChain.doFilter(request, response);
    }
}
