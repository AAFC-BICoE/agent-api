package ca.gc.aafc.agent.api.entities;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Identifiers {
  private List<Identifier> identifiers = new ArrayList<>();
}
