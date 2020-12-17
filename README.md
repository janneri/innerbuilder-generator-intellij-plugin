# innerbuilder-generator-intellij-plugin

![Build](https://github.com/janneri/innerbuilder-generator-intellij-plugin/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)

## Description 

<!-- Plugin description -->
Generates an inner builder class to a data class / DTO.
Supports recreation: just add a field to the data class and run generate again.<br>

You can configure the generator using the action "Generate Inner Builder...".
The settings are saved and survive IDE restarts.
To quickly generate a builder with the previously selected settings, just hit "shift alt B". 
<!-- Plugin description end -->

## About data classes and DTOs

The idea behind this generator is that a DTO is
- A simple class that holds data and nothing more
- The fields are public final fields (immutability)
- Because of public final fields, contains no getters
- Because of immutability and the generated builder, contains no setters

I know that this topic is highly opinionated. That's why we have so many variations of the builder pattern (and plugins)
and you can pick the one, that suits your opinions.

## Installation

- Using IDE built-in plugin system:
  
  <kbd>Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "innerbuilder-generator-intellij-plugin"</kbd> >
  <kbd>Install Plugin</kbd>
  
- Manually:

  Download the [latest release](https://github.com/janneri/innerbuilder-generator-intellij-plugin/releases/latest) and install it manually using
  <kbd>Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
