package com.ShopmeFrontEnd.entity.readonly;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "settings")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Setting implements Serializable {
    @Id
    @Column(name = "`key`", nullable = false, length = 123)
    private String key;

    @Column(nullable = false, length = 1024)
    private String value;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 45)
    private SettingCategory category;

    public Setting(String key) {
        this.key = key;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if(getClass() != obj.getClass()) return false;

        Setting other = (Setting) obj;
        if(key == null) {
            if(other.key != null){ // Key hai hi nahi check kaise kare
                return  false;
            }
        }else if(!key.equals(other.key)){
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }
}
