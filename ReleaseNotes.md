# struts2-junit 0.2.2 #

  * Release with a fix that allow to execute the tests **without** the need to download the Struts's DTD files every time it executes, thereby greatly improving the test execution time.

# struts2-junit 0.2.1 #

  * Release to allow distribution of the library from Maven Central Repository.

# struts2-junit 0.2 #

  * Modified `@Config` annotation to support multiple Struts 2 XML configuration files.

Example use case: Say you are developing an [Struts2 plugin](http://struts.apache.org/2.2.3.1/docs/plugins.html), which commonly includes a `struts-plugin.xml` which is used to configure or initialize the plugin.

If your test depends on this plugin, then you can configure the test my annotating with:

`@Config(file={"struts-plugin.xml", "struts-test.xml"})`

This will load the following configurations, in this specific order:

  1. struts-default.xml (allways loaded first by default)
  1. struts-plugin.xml
  1. struts-test.xml

# struts2-junit 0.1 #

  * First public release.