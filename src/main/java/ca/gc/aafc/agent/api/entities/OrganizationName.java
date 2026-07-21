package ca.gc.aafc.agent.api.entities;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Builder
@Setter
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class OrganizationName implements Serializable {

  private String languageCode;

  private String name;

}
