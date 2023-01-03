package com.github.xvthomas.model;

// As no schema available, let's consider both name or people can be nullable
final case class NamedPeoples(name: Option[String], peoples: Option[List[People]])
