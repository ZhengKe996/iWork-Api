package top.fanzhengke.emos.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@ApiModel
public class UpdateMeetingInfoForm {
    @NotBlank
    private String title;

    @NotNull
    @Pattern(regexp = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-))$")
    private String date;

    private String place;

    @NotNull
    private long start;

    @NotNull
    private long end;

    @Range(min = 1,max = 2)
    private Byte type;

    @NotBlank
    private String members;

    @NotBlank
    private String desc;

    @Min(1)
    private Integer id;

    private String instanceId;
}
