package org.backend.user.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Builder;
import lombok.Getter;
import org.backend.user.enums.Status;

import javax.xml.bind.annotation.XmlRootElement;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName(value = "response")
@XmlRootElement(name = "response")
@Getter
@Builder
public class ServiceResponse<T> {
    private Status status;
    private String message;
    private T data;
}
