
import java.io.IOException;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.proxy.DirectProxyCoapResolver;
import org.eclipse.californium.proxy.ProxyHttpServer;
import org.eclipse.californium.proxy.resources.ForwardingResource;
import org.eclipse.californium.proxy.resources.ProxyCoapClientResource;
import org.eclipse.californium.proxy.resources.ProxyHttpClientResource;
/**
 * @author ebruno
 *
 * Http2CoAP: Insert in browser: URI:
 * http://localhost:8080/proxy/coap://localhost:PORT/target
 *
 * CoAP2CoAP: Insert in Copper: URI: coap://localhost:PORT/coap2coap Proxy:
 * coap://localhost:PORT/targetA
 *
 * CoAP2Http: Insert in Copper: URI: coap://localhost:PORT/coap2http Proxy:
 * http://lantersoft.ch/robots.txt
 */
public class CoapCrossProxy {

    private static final int PORT = 5683; 
            //Configuration.getStandard().get( CoapConfig.COAP_PORT );

    private CoapServer targetServerA;

    /**
     * A simple resource that responds to GET requests with a small response
     * containing the resource's name.
     */
    private static class TargetResource extends CoapResource {

        private int counter = 0;

        public TargetResource(String name) {
            super(name);
        }

        @Override
        public void handleGET(CoapExchange exchange) {
            exchange.respond("Response " + (++counter) + " from resource " + getName());
        }
    }

    public CoapCrossProxy() throws IOException {
        ForwardingResource coap2coap = new ProxyCoapClientResource("coap2coap");
        ForwardingResource coap2http = new ProxyHttpClientResource("coap2http");

        // Create CoAP Server on PORT with proxy resources form CoAP to CoAP and HTTP
        targetServerA = new CoapServer(8082);
        targetServerA.add(coap2coap);
        targetServerA.add(coap2http);
        //targetServerA.add(new TargetResource("target"));
        targetServerA.start();

        ProxyHttpServer httpServer = new ProxyHttpServer(8080);
        httpServer.setProxyCoapResolver(new DirectProxyCoapResolver(coap2coap));

        System.out.println("CoAP resource \"target\" available over HTTP at: http://localhost:8080/proxy/coap://localhost:PORT/target");
    }

    public static void main(String[] args) throws Exception {
        new CoapCrossProxy();
    }

}
