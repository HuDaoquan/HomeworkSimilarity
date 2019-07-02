package pers.hdq.function;

/**
 * 
 * @ClassName: Result 
 * @Description: 此类是DAO类，用来存储比较结果。结果类，不建议新增其他功能
 * @author HuDaoquan
 * @date 2019年7月1日 下午9:27:06 
 * @version v1.0
 */
public class Result implements Comparable<Result> {
	private String name1; // 文件名1
	private String name2; // 被比较的文件名
	private Double sim; // 总相似度
	private Double JaccardSim; // jaccard相似度
	private Double ConSim; // 余弦相似度
	private Double avgPicSim; // 图片相似度
	private String crib;

	/**
	 * @return crib
	 */
	public String getCrib() {
		return crib;
	}

	/**
	 * @param crib 要设置的 crib
	 */
	public void setCrib(String crib) {
		this.crib = crib;
	}

	/**
	 * @return avgPicSim
	 */
	public Double getAvgPicSim() {
		return avgPicSim;
	}

	/**
	 * @param avgPicSim 要设置的 avgPicSim
	 */
	public void setAvgPicSim(Double avgPicSim) {
		this.avgPicSim = avgPicSim;
	}

	/**
	 * @return name1
	 */
	public String getName1() {
		return name1;
	}

	/**
	 * @param name1 要设置的 name1
	 */
	public void setName1(String name1) {
		this.name1 = name1;
	}

	/**
	 * @return name2
	 */
	public String getName2() {
		return name2;
	}

	/**
	 * @param name2 要设置的 name2
	 */
	public void setName2(String name2) {
		this.name2 = name2;
	}

	/**
	 * @return sim
	 */
	public Double getSim() {
		return sim;
	}

	/**
	 * @param sim 要设置的 sim
	 */
	public void setSim(Double sim) {
		this.sim = sim;
	}

	/**
	 * @return jaccardSim
	 */
	public Double getJaccardSim() {
		return JaccardSim;
	}

	/**
	 * @param jaccardSim 要设置的 jaccardSim
	 */
	public void setJaccardSim(Double jaccardSim) {
		JaccardSim = jaccardSim;
	}

	/**
	 * @return conSim
	 */
	public Double getConSim() {
		return ConSim;
	}

	/**
	 * @param conSim 要设置的 conSim
	 */
	public void setConSim(Double conSim) {
		ConSim = conSim;
	}

	@Override
	public int compareTo(Result arg0) {
		// 这里定义排序的规则。
		return arg0.getSim().compareTo(this.getSim());// 降序
//        return this.getSim().compareTo(arg0.getSim()); //升序
	}
}
