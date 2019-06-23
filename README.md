# BikingdataAndroid
Bikingdata (Android End)

- This App provides users access to mark biking lanes and speical points
(scenery points, entrance points, road construction, bad road condition, dead end) on the map.

- Users can store their data on their device, and also upload them to the server.

- User can also see points and lanes marked by others in the neighbouring areas. When uploading their 
data, users can set whether it's public or private. Other users can only view public points or routes.

- They can set the initial zoom, range of data displayed on map (centered around the current location)
  , and type of map in the App's settings.

- Puclic points and lanes are subject to upvotes and downvotes, users can tag their reasons for downvoting.
If the point or lane subjects to a lot of downvotes, it will disappear.

- If a point stands for bad lane condition or dead end lane, 
other users can override the point by uploading a photo of the repaired lane(only taken from camera, 
the location of the photo will be checked in order for the request to be valid),
or upvote to affirm the condition.

- Users can do custom searching for points or routes in the App. 
The results are determined by the filters they set.

Future function: 
Auto blurring and checking of the photos. 
(Blur human faces and license plates, check if the photo is a proper scenery photo.)

### TODO：
- 编写设置页面
- 修正添加图片页面的layout（在真机上运行排版混乱）
- 编写轨迹记录完成后的标注页面（可以参考地点的）
- 轨迹的起始结束markup图形添加
- 地点标注页面添加标注点类型（景色，关键位置，路况差，断头）的选择框
- 标注页面添加tag输入框
- 将创建的object中的内容存到本地数据库（Room）
- 细化description栏目（更多的选项框），添加字数限制
- 标注的删除功能（在图片标注显示页的delete按键）
- 编写并保留与后端连接的窗口，后端建立后，编写同步功能（需登录）, 
标注显示页的赞和踩， 以及特殊图片标注类的override功能
