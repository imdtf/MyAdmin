package com.dtf.modules.system.domain;

import com.dtf.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/11 22:22
 */
@Entity
@Getter
@Setter
@Table(name = "sys_user")
public class User extends BaseEntity implements Serializable {
    @Id
    @Column(name = "user_id")
    @NotNull(groups = {Update.class}, message = "修改时id不能为空")
    @Null(groups = {Create.class}, message = "创建时id必须为空")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "ID", hidden = true)
    private Long id;

    @ManyToMany
    @ApiModelProperty(value = "用户角色")
    @JoinTable(name = "sys_users_roles",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "role_id")})
    private Set<Role> roles;

    @ManyToMany
    @ApiModelProperty(value = "用户岗位")
    @JoinTable(name = "sys_users_jobs",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "job_id", referencedColumnName = "job_id")})
    private Set<Job> jobs;

    @OneToOne
    @JoinColumn(name = "dept_id")
    @ApiModelProperty(value = "用户部门")
    private Dept dept;

    @NotBlank
    @Column(unique = true)
    @ApiModelProperty(value = "用户名称")
    private String username;

    @NotBlank
    @ApiModelProperty(value = "用户昵称")
    private String nickName;

    @Email
    @NotBlank
    @ApiModelProperty(value = "邮箱")
    private String email;

    @NotBlank
    @ApiModelProperty(value = "电话号码")
    private String phone;

    @ApiModelProperty(value = "用户性别")
    private String gender;

    @ApiModelProperty(value = "头像名称", hidden = true)
    private String avatarName;


    @ApiModelProperty(value = "头像存储路径", hidden = true)
    private String avatarPath;

    @ApiModelProperty(value = "密码", hidden = true)
    private String password;

    @NotNull
    @ApiModelProperty(value = "是否启用")
    private Boolean enabled;

    @ApiModelProperty(value = "是否为Admin账号", hidden = true)
    private Boolean isAdmin = false;

    @Column(name = "pwd_reset_time")
    @ApiModelProperty(value = "最后修改密码是时间", hidden = true)
    private Date pwdResetTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(user.getId(), id) && Objects.equals(user.getUsername(), username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }
}
