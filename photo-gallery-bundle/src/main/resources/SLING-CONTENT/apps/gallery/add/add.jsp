<html>
<body>
<form method="POST" action="/content/gallery/photos/" enctype="multipart/form-data">
    <input type="hidden" name="sling:resourceType" value="gallery/photo"/>
    <textarea rows="5" name="description"></textarea>
    <input type="file" name="picture">
    <input type="hidden" id="lastModified" name="lastModified" value=""/>
    <input type="hidden" name=":redirect" value="*.html"/>
    <input type="submit" value="Add picture"/>
</form>
</body>
</html>