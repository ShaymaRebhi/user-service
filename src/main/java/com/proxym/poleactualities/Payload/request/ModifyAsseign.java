package com.proxym.poleactualities.Payload.request;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "Update asseign Request", description = "The update asseign request payload")
public class ModifyAsseign {


    @ApiModelProperty(required = false)
    private List<Long> idUser;

}
