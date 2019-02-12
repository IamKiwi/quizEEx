package javax.faces.simplesecurity;

import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Filter implementing security control in Java web applications.
 * 
 * @author Przemysław Kudłacik
 */
public class SecurityFilter implements Filter {
	private String noPermitionPage;
	private HashSet<String> publicResources = new HashSet<String>();
	private Hashtable<String, HashSet<String>> permitions = new Hashtable<String, HashSet<String>>();
	private boolean invlidateOnHostChange = false;
	private boolean invlidateOnAddrChange = false;
	private boolean invlidateOnPortChange = false;

	private static final String FACES_REDIRECT_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<partial-response><redirect url=\"%s\"></redirect></partial-response>";

	/**
	 * The method loads parameters specifying paths of login page (no permission
	 * page), permissions and automatic session invalidate (on host, address or port
	 * change). Parameters should be set for the filter in web.xml
	 * (noPermissionPage, permissions, invalidateOnRemoteHostChange, ...AddrChange
	 * and ...PortChange). Default values if not set: "/login.xhtml" as
	 * noPermissionPage, automatic session invalidation set to "false".
	 * 
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig config) throws ServletException {
		// Load permissions
		// - list of permissions in the following format (all whitespaces are trimmed):
		// resource_prefix : role_name[, role_name];
		String perms = config.getInitParameter("permissions");
		if (perms != null) {
			String[] list = perms.split(";");
			for (String perm : list) {
				perm = perm.trim();
				if (!perm.isEmpty()) {
					String[] row = perm.split(":");
					String resource = row[0].trim();
					if (!resource.isEmpty()) { // if resource is not empty
						HashSet<String> roles = new HashSet<String>();
						if (row.length > 1) {
							String[] rawroles = row[1].split(",");
							for (String role : rawroles) {
								role = role.trim();
								if ("PUBLIC".equals(role)) {
									role = "";
								}
								if (!role.isEmpty()) {
									roles.add(role);
								}
							}
						}
						if (roles.size() == 0) { // no roles - public resource
							publicResources.add(resource);
						} else { // roles specified - add to list of permissions
							permitions.put(resource, roles);
						}

					}
				}
			}
		}
		// load no permission page
		noPermitionPage = config.getInitParameter("noPermitionPage");
		if (noPermitionPage == null) {
			noPermitionPage = "/login.xhtml";
		}
		publicResources.add(noPermitionPage);
		// load auto invalidate configuration
		String param = config.getInitParameter("invalidateOnRemoteHostChange");
		if (param != null && "TRUE".equals(param.toUpperCase())) {
			invlidateOnHostChange = true;
		}
		param = config.getInitParameter("invalidateOnRemoteAddressChange");
		if (param != null && "TRUE".equals(param.toUpperCase())) {
			invlidateOnAddrChange = true;
		}
		param = config.getInitParameter("invalidateOnRemotePortChange");
		if (param != null && "TRUE".equals(param.toUpperCase())) {
			invlidateOnPortChange = true;
		}
	}

	/**
	 * The filter method retrieves a RemoteClient object from session. If the
	 * conditions of valid connection are fulfilled (access to public resource or
	 * appropriate permissions) then the request is passed further. In other case
	 * the user is forwarded to a configured noPermissionPage. The approach works
	 * also for JSF AJAX requests (if no permission on AJAX request the browser is
	 * redirected to contextPath using JSF partial response).
	 * 
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		HttpSession session = request.getSession();

		// load RemoteClient object from session
		RemoteClient client = RemoteClient.load(session);

		boolean pass = false;

		String path = request.getServletPath();// path of requested resource (without host, port and app name)
		// pass if public resource
		for (String resource : publicResources) {
			if (path.startsWith(resource)) {
				pass = true;
			}
		}

		if (!pass && client != null) { // check permissions if logged in
			int idx = path.length(); // first substring equals full path
			do {
				path = path.substring(0, idx); // delete last segment in path
				if (path.isEmpty()) {
					path = "/";
				}
				if (permitions.containsKey(path)) {
					HashSet<String> roles = permitions.get(path);
					if (client.isInOneRole(roles)) {
						pass = true;
					}
					break; // permission definition found, finish
				}
				idx = path.lastIndexOf("/"); // find index of last segment
			} while (path.length() > 1);

			// invalidate when host changed (if configured)
			if (invlidateOnHostChange && client.getRemoteHost() != null) {
				if (!request.getRemoteHost().equals(client.getRemoteHost())) {
					session.invalidate();
				}
			}
			// invalidate when ip changed (if configured)
			if (invlidateOnAddrChange && client.getRemoteAddr() != null) {
				if (!request.getRemoteAddr().equals(client.getRemoteAddr())) {
					session.invalidate();
				}
			}
			// invalidate when port changed (if configured)
			if (invlidateOnPortChange) {
				if (request.getRemotePort() != client.getRemotePort()) {
					session.invalidate();
				}
			}

		}

		// if the request is not valid (client is not logged in)
		if (!pass) {
			// if AJAX request then redirect to application root
			if ("partial/ajax".equals(request.getHeader("Faces-Request"))) {
				res.setContentType("text/xml");
				res.setCharacterEncoding("UTF-8");
				res.getWriter().printf(FACES_REDIRECT_XML, request.getContextPath() + "/");
			} else {
				// if regular request then forward to the defined noPermition page
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				request.getServletContext().getRequestDispatcher(noPermitionPage).forward(request, response);
			}

		} else { // if request is valid (client is logged in) then
			chain.doFilter(request, response);
		}

	}

	@Override
	public void destroy() {
	}
}
