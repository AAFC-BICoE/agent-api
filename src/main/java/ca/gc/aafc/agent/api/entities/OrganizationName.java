package ca.gc.aafc.agent.api.entities;

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
public class OrganizationName {

  private String languageCode;

  private String name;

}
