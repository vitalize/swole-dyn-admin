[![Travis](https://img.shields.io/travis/vitalize/swole-dyn-admin.svg)](https://travis-ci.org/vitalize/swole-dyn-admin)
[![Maven Central](https://img.shields.io/maven-central/v/com.vitalize/swole-dyn-admin.svg)](https://search.maven.org/#search%7Cga%7C1%7Ca%3A%22swole-dyn-admin%22)
[![GitHub release](https://img.shields.io/github/release/vitalize/swole-dyn-admin.svg)](https://github.com/vitalize/swole-dyn-admin/releases)
[![Gitter](https://img.shields.io/gitter/room/vitalize/swole-dyn-admin.svg)](https://gitter.im/vitalize/swole-dyn-admin)
[![Codacy grade](https://img.shields.io/codacy/grade/33f5b076d8d24896a60588195209c99e.svg)](https://www.codacy.com/app/drdamour/swole-dyn-admin)
[![Codacy coverage](https://img.shields.io/codacy/coverage/33f5b076d8d24896a60588195209c99e.svg)](https://www.codacy.com/app/drdamour/swole-dyn-admin)
[![Coveralls](https://img.shields.io/coveralls/vitalize/swole-dyn-admin.svg)](https://coveralls.io/github/vitalize/swole-dyn-admin)

# swole-dyn-admin
A collection of classes that can make your Oracle ATG Commerce Dyn Admin Swole.

## What?
This project makes the dyn admin swole by tweaking the ouput to add various features.  These features are explained in the subsections.

### GSA Admin Servlet Enhancements
The GSA Admin Servlet is responsbile for drawing the GSARepository admin interface.  The page you probably spend a lot of time running arbitraty RQL within.  Swole Dyn Admin makes this servlet swole with a couple of useful features.

#### Jump to Query Box
If you have ever had a repo with a lot of item descriptors..you are used to scrolling down to get to the query box.  Swole Dyn Admin adds a convenient hyperlink to jump you down to RQL box.
![Jump Around, Jump Around, Get up, Get Up, and Get Down...jump..jump..jump](/screenshots/SwoleGSAAdminServlet/jump-to-query.png)

#### RQL Toolbar
If you have used RQL you have certainly typed `<query-items item-descriptor=...` enough in your life.  Now you can point and click yourself to constructing the most basic queries!  Options for ordering and paging the results are also available, as well as a handy link to the online RQL overview.
![It looks terrible, and it functions only slightly better](/screenshots/SwoleGSAAdminServlet/rql-toolbar.png)

It's very likely you often want to query for something by its ID.  The Append Query by ID button is made for this, it appends a query and moves the focus of the cursor to allow you to type in your ID quickly
![Saves precious microseconds](/screenshots/SwoleGSAAdminServlet/rql-toolbar-query-by-id.png)

#### Better RQL Box
The RQL Box is now bigger...but it's better too.  When you submit a query you no longer have to scroll down to see your results, instead the RQL box submits against a html fragment identifier for the results.  No screen shot can do this marvelousness justice.

#### Link to RQL Query
It's reasonable to assume a lot of the time you are doing RQL queries you are troubleshooting an issue.  An important component of troubleshooting is sharing discoveries with your team.  This has always been hard in the dyn admin because it requires sharing the link to the admin servlet, credentials, and usually the RQL query that shows the data.  With the SwoleGSAAdminServlet the first and third pieces are combined in a single link that will execute your `query-items` query!  What's more every `query-items` you perform now has a shareable link generated for it.
![Take this and shove it in your Jira ticket](/screenshots/SwoleGSAAdminServlet/link-to-rql-query.png)

## How?
The jar contains a bunch of extensions to common ATG nucleus classes.  Drop properties files that override the class used with the swole version.  

For example suppose you have a GSARepository at /atg/commerce/catalog/ProductCatalog then all you need to do is add swole-dyn-admin.jar to your module's MANIFEST.MF ATG-Class-Path: and drop a /atg/commerce/catalog/ProductCatalog.properties file with the following contents:
```
$class=com.vitalize.atg.adapter.gsa.SwoleGSARepository
```


## Why?
If you use or develop with Oracle ATG Commerce, then you certainly are a Dyn Admin user.  Vitalize thought it would be better to have swole versions of things when developing [bodybuilding.com](https://www.bodybuilding.com/store)
 
## Contribute?
Ideas are needed!  Everyone has probably said "I wish dyn admin would..." at some point, so now you have a place to express that wish.  Just open an issue.

We also happily accept pull requests. The nature of the project is that it's hard to verify a feature works on all versions, so be prepared to rework some things to make them more version agnostics..possibly.  The dyn admin doesn't change much...so maybe it will all be ok.  We appreciate reports of incompatibilities.

## Differences from BetterDynAdmin
This project is very similar to the excellent [BetterDynAdmin](https://github.com/jc7447/BetterDynAdmin).  Surprisingly this project was created without knowing of BetterDynAdmin's existence...but we came to some very similar UX.  The big difference is BetterDynAdmin is a grease monkey based client side manipulation of the existing dyn-admin while swole-dyn-admin is a server side modification of ATG.  The former requires installation in every machine of every user (with very easy steps) while the latter requires no installation on client devices.

## Disclaimer

This project is completely unofficial and in no way endorsed by Oracle Corporation. Oracle ATG web commerce is a copyrighted product of Oracle Corporation and no rights are contested by this project or it contents.

## LICENSE
Copyright 2017 Vitalize LLC

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.





