import qupath.lib.images.servers.ImageServer
import qupath.lib.objects.PathObject
import qupath.lib.images.writers.PngWriter

import javax.imageio.ImageIO
import java.awt.Color
import java.awt.image.BufferedImage

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

// Get the main QuPath data structures
def imageData = getCurrentImageData()
def hierarchy = imageData.getHierarchy()
def server = imageData.getServer()
def plane = ImagePlane.getPlane(0, 0)
def region_name = 'LA'

// Request all objects from the hierarchy & filter only the detections belonging to specified path class
def cells = getDetectionObjects()
def size = 64
// Define downsample value for export resolution & output directory, creating directory if necessary
def downsample = 1
def pathOutput = buildFilePath(QPEx.PROJECT_BASE_DIR, 'cells')
mkdirs(pathOutput)

// Define image export type; valid values are JPG, PNG or null (if no image region should be exported with the mask)
// Note: masks will always be exported as PNG

def calh = server.getPixelCalibration().getPixelHeightMicrons()
def calw = server.getPixelCalibration().getPixelWidthMicrons()
def count = 1
def centroids = []
// Export each annotation
cells.findAll{measurement(it, "Nucleus: Area µm^2")>50}.each {
    def roi = it.getROI()
    if (roi == null) {
        print 'Warning! No ROI for object ' + it + ' - cannot export corresponding region & mask'
        return
    }

    // Create a region from the ROI
    //def region = RegionRequest.createInstance(server.getPath(), downsample, roi)

    // Create a name
    String name = String.format('%s_%d',
            server.getMetadata().getName().substring(0,9)+'_'+server.getMetadata().getName().substring(20,22),
            count
    )
    
    if (it.getPathClass().toString().contains('Myc')) {
        centroids = centroids + [server.getMetadata().getName(), 1, roi.getCentroidX()*calw, roi.getCentroidY()*calh, name]
    }
    //else if (it.getPathClass().toString().contains('Negative')) {
    //    centroids = centroids + [count, 'eGFP-', roi.getCentroidX()*calw, roi.getCentroidY()*calh, name+'.tif']
    //}
    else {
        centroids = centroids + [server.getMetadata().getName(), 0, roi.getCentroidX()*calw, roi.getCentroidY()*calh, name]
    }

    def roiRegion = ROIs.createRectangleROI(roi.getCentroidX()-size/2, roi.getCentroidY()-size/2, size, size, plane)
    def region = RegionRequest.createInstance(server.getPath(), downsample, roiRegion)
    // Request the BufferedImage
    def img = server.readBufferedImage(region)
    def fileMask = new File(pathOutput, name + '.tif').toString()

    writeImage(img, fileMask)
    ++count
}
String name = server.getMetadata().getName().substring(0,9)+'_'+server.getMetadata().getName().substring(20,22)
def fileCSV = new File(pathOutput, name + '-centroids.csv')
CSVExporter(fileCSV, centroids)

print 'Done!'

def CSVExporter ( File file, ArrayList array) {
    try (FileOutputStream stream = new FileOutputStream(file)) {
    try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(stream, StandardCharsets.UTF_8))) { 
    for (int nCol = 0; nCol < array.size(); nCol++) {
        writer.write(array[nCol].toString())
	if (nCol%5==4) writer.write('\n')
	else writer.write(',')
    }
    }
    catch (Exception e) {
	logger.error("Error writing to file: " + e.getLocalizedMessage(), e);
    }
    }
    catch (Exception e) {
	logger.error("Error opening stream: " + e.getLocalizedMessage(), e);
    }
    print 'CSV good'
}