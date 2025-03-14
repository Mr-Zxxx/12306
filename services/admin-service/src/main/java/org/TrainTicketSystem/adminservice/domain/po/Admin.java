package org.TrainTicketSystem.adminservice.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理员表
 * @TableName t_admin
 */
@TableName(value ="t_admin")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Admin implements Serializable {
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 国家/地区
     */
    private String region;

    /**
     * 证件类型
     */
    private Integer idType;

    /**
     * 证件号
     */
    private String idCard;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 固定电话
     */
    private String telephone;

    /**
     * 邮箱
     */
    private String mail;

    /**
     * 审核状态
     */
    private Integer verifyStatus;

    /**
     * 邮编
     */
    private String postCode;

    /**
     * 地址
     */
    private String address;

    /**
     * 注销时间戳
     */
    private Long deletionTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 管理员标识
     */
    private Integer adminFlag;

    /**
     * 删除标识
     */
    private Integer delFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

//    @Override
//    public boolean equals(Object that) {
//        if (this == that) {
//            return true;
//        }
//        if (that == null) {
//            return false;
//        }
//        if (getClass() != that.getClass()) {
//            return false;
//        }
//        Admin other = (Admin) that;
//        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
//            && (this.getUsername() == null ? other.getUsername() == null : this.getUsername().equals(other.getUsername()))
//            && (this.getPassword() == null ? other.getPassword() == null : this.getPassword().equals(other.getPassword()))
//            && (this.getRealName() == null ? other.getRealName() == null : this.getRealName().equals(other.getRealName()))
//            && (this.getRegion() == null ? other.getRegion() == null : this.getRegion().equals(other.getRegion()))
//            && (this.getIdType() == null ? other.getIdType() == null : this.getIdType().equals(other.getIdType()))
//            && (this.getIdCard() == null ? other.getIdCard() == null : this.getIdCard().equals(other.getIdCard()))
//            && (this.getPhone() == null ? other.getPhone() == null : this.getPhone().equals(other.getPhone()))
//            && (this.getTelephone() == null ? other.getTelephone() == null : this.getTelephone().equals(other.getTelephone()))
//            && (this.getMail() == null ? other.getMail() == null : this.getMail().equals(other.getMail()))
//            && (this.getVerifyStatus() == null ? other.getVerifyStatus() == null : this.getVerifyStatus().equals(other.getVerifyStatus()))
//            && (this.getPostCode() == null ? other.getPostCode() == null : this.getPostCode().equals(other.getPostCode()))
//            && (this.getAddress() == null ? other.getAddress() == null : this.getAddress().equals(other.getAddress()))
//            && (this.getDeletionTime() == null ? other.getDeletionTime() == null : this.getDeletionTime().equals(other.getDeletionTime()))
//            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
//            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
//            && (this.getAdminFlag() == null ? other.getAdminFlag() == null : this.getAdminFlag().equals(other.getAdminFlag()))
//            && (this.getDelFlag() == null ? other.getDelFlag() == null : this.getDelFlag().equals(other.getDelFlag()));
//    }
//
//    @Override
//    public int hashCode() {
//        final int prime = 31;
//        int result = 1;
//        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
//        result = prime * result + ((getUsername() == null) ? 0 : getUsername().hashCode());
//        result = prime * result + ((getPassword() == null) ? 0 : getPassword().hashCode());
//        result = prime * result + ((getRealName() == null) ? 0 : getRealName().hashCode());
//        result = prime * result + ((getRegion() == null) ? 0 : getRegion().hashCode());
//        result = prime * result + ((getIdType() == null) ? 0 : getIdType().hashCode());
//        result = prime * result + ((getIdCard() == null) ? 0 : getIdCard().hashCode());
//        result = prime * result + ((getPhone() == null) ? 0 : getPhone().hashCode());
//        result = prime * result + ((getTelephone() == null) ? 0 : getTelephone().hashCode());
//        result = prime * result + ((getMail() == null) ? 0 : getMail().hashCode());
//        result = prime * result + ((getVerifyStatus() == null) ? 0 : getVerifyStatus().hashCode());
//        result = prime * result + ((getPostCode() == null) ? 0 : getPostCode().hashCode());
//        result = prime * result + ((getAddress() == null) ? 0 : getAddress().hashCode());
//        result = prime * result + ((getDeletionTime() == null) ? 0 : getDeletionTime().hashCode());
//        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
//        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
//        result = prime * result + ((getAdminFlag() == null) ? 0 : getAdminFlag().hashCode());
//        result = prime * result + ((getDelFlag() == null) ? 0 : getDelFlag().hashCode());
//        return result;
//    }
//
//    @Override
//    public String toString() {
//        StringBuilder sb = new StringBuilder();
//        sb.append(getClass().getSimpleName());
//        sb.append(" [");
//        sb.append("Hash = ").append(hashCode());
//        sb.append(", id=").append(id);
//        sb.append(", username=").append(username);
//        sb.append(", password=").append(password);
//        sb.append(", realName=").append(realName);
//        sb.append(", region=").append(region);
//        sb.append(", idType=").append(idType);
//        sb.append(", idCard=").append(idCard);
//        sb.append(", phone=").append(phone);
//        sb.append(", telephone=").append(telephone);
//        sb.append(", mail=").append(mail);
//        sb.append(", verifyStatus=").append(verifyStatus);
//        sb.append(", postCode=").append(postCode);
//        sb.append(", address=").append(address);
//        sb.append(", deletionTime=").append(deletionTime);
//        sb.append(", createTime=").append(createTime);
//        sb.append(", updateTime=").append(updateTime);
//        sb.append(", adminFlag=").append(adminFlag);
//        sb.append(", delFlag=").append(delFlag);
//        sb.append(", serialVersionUID=").append(serialVersionUID);
//        sb.append("]");
//        return sb.toString();
//    }
}