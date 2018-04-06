
package org.apache.geode.connectors.jdbc.internal.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="connection" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                 &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="url" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="user" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="password" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="parameters" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="region-mapping" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="field-mapping" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;simpleContent>
 *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                           &lt;attribute name="field-name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="column-name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/extension>
 *                       &lt;/simpleContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="connection-name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="region" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="table" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="pdx-class" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="primary-key-in-value" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" fixed="connector-service" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "connection",
    "regionMapping"
})
@XmlRootElement(name = "connector-service", namespace = "http://geode.apache.org/schema/jdbc")
public class ConnectorService {
    public static String SCHEMA = "http://geode.apache.org/schema/jdbc http://geode.apache.org/schema/jdbc/jdbc-1.0.xsd";

    @XmlElement(namespace = "http://geode.apache.org/schema/jdbc")
    protected List<ConnectorService.Connection> connection;
    @XmlElement(name = "region-mapping", namespace = "http://geode.apache.org/schema/jdbc")
    protected List<ConnectorService.RegionMapping> regionMapping;
    @XmlAttribute(name = "name")
    protected String name;

    /**
     * Gets the value of the connection property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the connection property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getConnection().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ConnectorService.Connection }
     * 
     * 
     */
    public List<ConnectorService.Connection> getConnection() {
        if (connection == null) {
            connection = new ArrayList<ConnectorService.Connection>();
        }
        return this.connection;
    }

    /**
     * Gets the value of the regionMapping property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the regionMapping property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRegionMapping().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ConnectorService.RegionMapping }
     * 
     * 
     */
    public List<ConnectorService.RegionMapping> getRegionMapping() {
        if (regionMapping == null) {
            regionMapping = new ArrayList<ConnectorService.RegionMapping>();
        }
        return this.regionMapping;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        if (name == null) {
            return "connector-service";
        } else {
            return name;
        }
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="url" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="user" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="password" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="parameters" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/extension>
     *   &lt;/simpleContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Connection {
        @XmlAttribute(name = "name")
        protected String name;
        @XmlAttribute(name = "url")
        protected String url;
        @XmlAttribute(name = "user")
        protected String user;
        @XmlAttribute(name = "password")
        protected String password;
        @XmlAttribute(name = "parameters")
        protected String parameters;

        /**
         * Gets the value of the name property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the value of the name property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setName(String value) {
            this.name = value;
        }

        /**
         * Gets the value of the url property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getUrl() {
            return url;
        }

        /**
         * Sets the value of the url property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setUrl(String value) {
            this.url = value;
        }

        /**
         * Gets the value of the user property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getUser() {
            return user;
        }

        /**
         * Sets the value of the user property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setUser(String value) {
            this.user = value;
        }

        /**
         * Gets the value of the password property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPassword() {
            return password;
        }

        /**
         * Sets the value of the password property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPassword(String value) {
            this.password = value;
        }

        /**
         * Gets the value of the parameters property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getParameters() {
            return parameters;
        }

        /**
         * Sets the value of the parameters property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setParameters(String value) {
            this.parameters = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="field-mapping" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;simpleContent>
     *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *                 &lt;attribute name="field-name" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="column-name" type="{http://www.w3.org/2001/XMLSchema}string" />
     *               &lt;/extension>
     *             &lt;/simpleContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *       &lt;attribute name="connection-name" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="region" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="table" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="pdx-class" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="primary-key-in-value" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "fieldMapping"
    })
    public static class RegionMapping {

        @XmlElement(name = "field-mapping", namespace = "http://geode.apache.org/schema/jdbc")
        protected List<ConnectorService.RegionMapping.FieldMapping> fieldMapping;
        @XmlAttribute(name = "connection-name")
        protected String connectionName;
        @XmlAttribute(name = "region")
        protected String region;
        @XmlAttribute(name = "table")
        protected String table;
        @XmlAttribute(name = "pdx-class")
        protected String pdxClass;
        @XmlAttribute(name = "primary-key-in-value")
        protected String primaryKeyInValue;

        /**
         * Gets the value of the fieldMapping property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the fieldMapping property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getFieldMapping().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ConnectorService.RegionMapping.FieldMapping }
         * 
         * 
         */
        public List<ConnectorService.RegionMapping.FieldMapping> getFieldMapping() {
            if (fieldMapping == null) {
                fieldMapping = new ArrayList<ConnectorService.RegionMapping.FieldMapping>();
            }
            return this.fieldMapping;
        }

        /**
         * Gets the value of the connectionName property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getConnectionName() {
            return connectionName;
        }

        /**
         * Sets the value of the connectionName property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setConnectionName(String value) {
            this.connectionName = value;
        }

        /**
         * Gets the value of the region property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getRegion() {
            return region;
        }

        /**
         * Sets the value of the region property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setRegion(String value) {
            this.region = value;
        }

        /**
         * Gets the value of the table property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTable() {
            return table;
        }

        /**
         * Sets the value of the table property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTable(String value) {
            this.table = value;
        }

        /**
         * Gets the value of the pdxClass property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPdxClass() {
            return pdxClass;
        }

        /**
         * Sets the value of the pdxClass property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPdxClass(String value) {
            this.pdxClass = value;
        }

        /**
         * Gets the value of the primaryKeyInValue property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPrimaryKeyInValue() {
            return primaryKeyInValue;
        }

        /**
         * Sets the value of the primaryKeyInValue property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPrimaryKeyInValue(String value) {
            this.primaryKeyInValue = value;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;simpleContent>
         *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
         *       &lt;attribute name="field-name" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="column-name" type="{http://www.w3.org/2001/XMLSchema}string" />
         *     &lt;/extension>
         *   &lt;/simpleContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "value"
        })
        public static class FieldMapping {

            @XmlValue
            protected String value;
            @XmlAttribute(name = "field-name")
            protected String fieldName;
            @XmlAttribute(name = "column-name")
            protected String columnName;

            /**
             * Gets the value of the value property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getValue() {
                return value;
            }

            /**
             * Sets the value of the value property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setValue(String value) {
                this.value = value;
            }

            /**
             * Gets the value of the fieldName property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getFieldName() {
                return fieldName;
            }

            /**
             * Sets the value of the fieldName property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setFieldName(String value) {
                this.fieldName = value;
            }

            /**
             * Gets the value of the columnName property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getColumnName() {
                return columnName;
            }

            /**
             * Sets the value of the columnName property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setColumnName(String value) {
                this.columnName = value;
            }

        }

    }

}
