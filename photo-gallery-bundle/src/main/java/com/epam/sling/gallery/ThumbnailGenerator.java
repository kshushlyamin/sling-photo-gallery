package com.epam.sling.gallery;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.*;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFactory;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Calendar;

/**
 * User: kshushlyamin
 */
@Component(immediate = true)
@Service(value = EventHandler.class)
@Properties({
        @Property(name = "service.description",
                value = "Thumb creator service"),
        @Property(name = EventConstants.EVENT_TOPIC, value = org.apache.sling.api.SlingConstants.TOPIC_RESOURCE_ADDED)
})
public class ThumbnailGenerator implements EventHandler {
    public static final String JPG = "jpg";
    public static final String THUMBNAILS = "thumbnails";
    public static final String GALLERY_PHOTOS = "/gallery/photos/";
    public static final String PICTURE = "/picture";
    public static final int WIDTH = 100;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    protected void activate(ComponentContext context) throws Exception {
        logger.info("Custom activate");
    }

    protected void deactivate(ComponentContext componentContext) throws RepositoryException {
        logger.info("Custom deactivate");
    }

    @Override
    public void handleEvent(Event event) {
        final String path = (String) event.getProperty(org.apache.sling.api.SlingConstants.PROPERTY_PATH);
        logger.info("Create thumbnail for " + path);
        if (path != null && path.contains(GALLERY_PHOTOS) && path.contains(PICTURE) && !path.contains(THUMBNAILS)) {
            ResourceResolver resolver = null;
            try {
                resolver = this.resourceResolverFactory.getAdministrativeResourceResolver(null);
                final Resource r = resolver.getResource(path);
                final Node node = r.adaptTo(Node.class);

                final ByteArrayOutputStream os = getOriginalPicture(node);

                final Node thumbNode = createThumbnailNode(node, os);

                thumbNode.getSession().save();

                logger.info("Created thumbnail " + thumbNode.getPath());
            } catch (PathNotFoundException e) {
                logger.error("jcr:data not found", e);
            } catch (RepositoryException re) {
                logger.error("Sling Repo excpetion", re);
            } catch (final LoginException e) {
            } catch (IOException e) {
                logger.error("Processing image error", e);
            } finally {
                if (resolver != null) {
                    resolver.close();
                }
            }
        }
    }

    private ByteArrayOutputStream getOriginalPicture(Node node) throws RepositoryException, IOException {
        final InputStream is = node.getProperty("jcr:data").getBinary().getStream();
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        scale(is, WIDTH, os, JPG);
        return os;
    }

    private Node createThumbnailNode(Node pictureNode, ByteArrayOutputStream os) throws RepositoryException {
        final Node folder = pictureNode.getParent().addNode(THUMBNAILS, "nt:folder");
        final Node thumb = folder.addNode(pictureNode.getName() + "_100", "nt:file");
        final Node res = thumb.addNode("jcr:content", "nt:resource");
        final ValueFactory valueFactory = res.getSession().getValueFactory();

        res.setProperty("jcr:data", valueFactory.createBinary(new ByteArrayInputStream(os.toByteArray())));
        res.setProperty("jcr:lastModified", Calendar.getInstance());
        res.setProperty("jcr:mimeType", pictureNode.getProperty("jcr:mimeType").getString());
        return res;
    }

    private void scale(InputStream inputStream, int width, OutputStream outputStream, String suffix) throws IOException {
        if (inputStream == null) {
            throw new IOException("InputStream is null");
        }

        final BufferedImage src = ImageIO.read(inputStream);
        if (src == null) {
            final StringBuffer sb = new StringBuffer();
            for (String fmt : ImageIO.getReaderFormatNames()) {
                sb.append(fmt);
                sb.append(' ');
            }
            throw new IOException("Unable to read image, registered formats: " + sb);
        }

        final double scale = (double) width / src.getWidth();

        int destWidth = width;
        int destHeight = new Double(src.getHeight() * scale).intValue();
        logger.info("Generating thumbnail, w={}, h={}", destWidth, destHeight);
        BufferedImage dest = new BufferedImage(destWidth, destHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = dest.createGraphics();
        AffineTransform at = AffineTransform.getScaleInstance((double) destWidth / src.getWidth(), (double) destHeight / src.getHeight());
        g.drawRenderedImage(src, at);
        ImageIO.write(dest, suffix, outputStream);
    }
}
