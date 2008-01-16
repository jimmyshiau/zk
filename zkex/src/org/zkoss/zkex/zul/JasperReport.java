/* JasperReport.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Jan 16 14:37:13     2008, Created by gracelin
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zkex.zul;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperRunManager;

import org.zkoss.lang.Objects;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.Media;
import org.zkoss.xml.HTMLs;
import org.zkoss.zk.ui.ext.render.DynamicMedia;
import org.zkoss.zul.impl.Utils;
import org.zkoss.zul.impl.XulElement;

/**
 * The JasperReports component.
 * 
 * @author gracelin
 * @since 3.0.2
 */
public class JasperReport extends XulElement {
	private static final long serialVersionUID = 1L;
	private String _src;
	private Map _parameters;
	private JRDataSource _dataSource;
	private int _medver;

	public JasperReport() {
		setHeight("100%");
		setWidth("100%");
	}

	public JasperReport(String src) {
		setSrc(src);
	}

	/**
	 * Returns the source.
	 * 
	 * @return src The compiled file (jasper file).
	 */
	public String getSrc() {
		return _src;
	}

	/**
	 * Sets the source.
	 * <p>
	 * If src is changed, the whole component is invalidate.
	 * 
	 * @param src
	 *            The compiled file (jasper file). If null or empty, nothing is included.
	 */
	public void setSrc(String src) {
		if (src != null && src.length() == 0)
			src = null;
		if (!Objects.equals(_src, src)) {
			_src = src;
			invalidate();
		}
	}

	/**
	 * Returns the attributes for generating the HTML tags.
	 */
	public String getOuterAttrs() {
		final StringBuffer sb = new StringBuffer(64).append(super
				.getOuterAttrs());
		if (_src == null)
			return sb.toString();

		StringTokenizer st = new StringTokenizer(_src, ".");
		HTMLs.appendAttribute(sb, "src", Utils.getDynamicMediaURI(this,
				_medver++, st.nextToken(), "pdf"));

		return sb.toString();
	}

	/**
	 * Returns the JasperReports Parameters.
	 */
	public Map getParameters() {
		return _parameters;
	}

	/**
	 * Sets the JasperReports Parameters.
	 * 
	 * @param parameters use to fill the report
	 */
	public void setParameters(Map parameters) {
		if (!Objects.equals(_parameters, parameters)) {
			_parameters = parameters;
		}
	}

	/**
	 * Returns the JasperReports DataSource.
	 */
	public JRDataSource getDataSource() {
		return _dataSource;
	}

	/**
	 * Sets the JasperReports DataSource.
	 * 
	 * @param dataSource use to fill the report
	 */
	public void setDataSource(JRDataSource dataSource) {
		if (!Objects.equals(_dataSource, dataSource)) {
			_dataSource = dataSource;
		}
	}

	// -- ComponentCtrl --//
	protected Object newExtraCtrl() {
		return new ExtraCtrl();
	}

	/**
	 * A utility class to implement {@link #getExtraCtrl}. It is used only by
	 * component developers.
	 */
	protected class ExtraCtrl implements DynamicMedia {
		// -- DynamicMedia --//
		public Media getMedia(String pathInfo) {
			return doReport();
		}
	}

	/**
	 * Use the Parameters & Data sourse to produce report.
	 * If parameters are null, we will use an empty Map. 
	 * If data source is null, use JREmptyDataSource.
	 * 
	 * @return A AMedia contains report��s byte stream.
	 */
	private AMedia doReport() {

		InputStream is = null;
		try {
			// generate report pdf stream
			is = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(_src);

			if (_parameters == null)
				_parameters = new HashMap();
			if (_dataSource == null)
				_dataSource = new JREmptyDataSource();

			final byte[] buf = JasperRunManager.runReportToPdf(is, _parameters,
					_dataSource);

			// prepare the AMedia
			final InputStream mediais = new ByteArrayInputStream(buf);
			final AMedia amedia = new AMedia("FirstReport.pdf", "pdf",
					"application/pdf", mediais);

			return amedia;

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
