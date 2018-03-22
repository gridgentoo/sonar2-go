package org.sonar.commonruleengine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.sonar.commonruleengine.checks.Check;
import org.sonar.uast.UastNode;

public class EngineContext {

  private List<Issue> issues = new ArrayList<>();

  private Map<UastNode.Kind, List<Check>> registeredChecks = new EnumMap<>(UastNode.Kind.class);
  private Set<Check> checks = null;

  public void register(UastNode.Kind kind, Check check) {
    registeredChecks.computeIfAbsent(kind, k -> new ArrayList<>()).add(check);
  }

  public List<Check> registeredChecks(UastNode.Kind kind) {
    return registeredChecks.getOrDefault(kind, Collections.emptyList());
  }

  public void reportIssue(Issue issue) {
    issues.add(issue);
  }

  public void enterFile() {
    issues.clear();
    getChecks().forEach(Check::enterFile);
  }

  public List<Issue> getIssues() {
    return new ArrayList<>(issues);
  }

  private Set<Check> getChecks() {
    if (checks == null) {
      checks = registeredChecks.values().stream().flatMap(List::stream).collect(Collectors.toSet());
    }
    return checks;
  }
}
