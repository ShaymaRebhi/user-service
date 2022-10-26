package com.proxym.poleactualities.Models;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@ToString
@Table(name = "roles")
public class Role implements Serializable {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;
        @Enumerated(EnumType.STRING)
        @Column(length = 40)
        private RoleName name;

        public Role() {
        }
        public Role(RoleName name) {
                this.name = name;
        }
        public Integer getId() {
                return id;
        }
        public void setId(Integer id) {
                this.id = id;
        }
        public RoleName getName() {
                return name;
        }
        public void setName(RoleName name) {
                this.name = name;
        }
}
