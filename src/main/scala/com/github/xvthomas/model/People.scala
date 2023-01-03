package com.github.xvthomas.model;

// As no schema available, let's consider both name or animals can be nullable
final case class People(name: Option[String], animals: Option[List[Animal]])
