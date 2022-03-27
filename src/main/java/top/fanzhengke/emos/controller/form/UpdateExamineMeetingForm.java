package top.fanzhengke.emos.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.*;

@Data
@ApiModel
public class UpdateExamineMeetingForm {


    @NotNull
    @Min(1)
    private Integer id;

    @NotNull
    @Min(1)
    @Max(2)
    private Integer status;

}
