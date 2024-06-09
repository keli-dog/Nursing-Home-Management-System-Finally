package com.ew.gerocomium.common.config.security.entity;

import com.ew.gerocomium.dao.vo.LoginUserVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserDetails implements UserDetails {
    private LoginUserVo loginUserVo;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 把permissions中String类型的权限信息封装成SimpleGrantedAuthority对象
        // 法一
//        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
//        for (String permission : permissions) {
//            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(permission);
//            authorityList.add(simpleGrantedAuthority);
//        }
        // 法二
//        List<SimpleGrantedAuthority> authorityList =
//                permissions.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        return null;
    }

    @Override
    public String getPassword() {
        return loginUserVo.getPass();
    }

    @Override
    public String getUsername() {
        return loginUserVo.getPhone();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
