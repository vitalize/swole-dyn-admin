# swole-dyn-admin
A collection of classes that can make your Oracle ATG Commerce Dyn Admin Swole.

## What?

### RQL Toolbar


### Bigger RQL Box


### Link to RQL Overview


## How?
The jar contains a bunch of extensions to common ATG nucleus classes.  Drop properties files that override the class used with the swole version.  

For example suppose you have a GSARepository at /atg/commerce/catalog/ProductCatalog then all you need to do is add swole-dyn-admin.jar to your module's MANIFEST.MF ATG-Class-Path: and drop a /atg/commerce/catalog/ProductCatalog.properties file with the following contents:
```
$class=com.vitalize.atg.adapter.gsa.SwoleGSARepository
```


## Why?
If you use or develop with Oracle ATG Commerce, then you certainly are a Dyn Admin user.  Vitalize thought it would be better to have swole versions of things.
 
## Contribute?

## LICENSE
Copyright 2017 Vitalize LLC

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.





