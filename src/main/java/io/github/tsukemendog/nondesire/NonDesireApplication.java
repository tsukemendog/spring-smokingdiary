package io.github.tsukemendog.nondesire;

import io.github.tsukemendog.nondesire.entity.ChallengeLevel;
import io.github.tsukemendog.nondesire.repository.ChallengeLevelRepository;
import io.github.tsukemendog.nondesire.repository.ProgressRepository;
import io.github.tsukemendog.nondesire.repository.MemberRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@SpringBootApplication
@EnableScheduling
public class NonDesireApplication {

	
	private final String currentDomain = "";

	public static void main(String[] args) {
		SpringApplication.run(NonDesireApplication.class, args);
	}



	@Bean
	ApplicationRunner applicationRunner(Environment environment) {
		return args -> {
			log.info("current imported properties profile : " + environment.getProperty("current-profile"));
		};
	}

	//https://openjdk.org/groups/net/httpclient/recipes.html httpclient 레시피
	@Bean
	@Profile("h2")
	public CommandLineRunner devInit(ChallengeLevelRepository challengeLevelRepository) {
		return (args) -> {

/*			//6.6일 단위
			log.info("========================= Challenge Level Insert ================================");
			ChallengeLevel challengeLevel0 = challengeLevelRepository.save(ChallengeLevel.builder().levelNumber(0L).reward("레벨0").duration(0L).image("").build()); //0분
			ChallengeLevel challengeLevel1 = challengeLevelRepository.save(ChallengeLevel.builder().levelNumber(1L).reward("레벨1 보상").duration(570240000L).image(currentDomain + "/image/levels/level1.png").title("레벨 1: 시작 단계").message("새로운 시작을 축하합니다! 금연의 첫걸음을 내딛는 것이 가장 중요합니다. 오늘 당신이 내린 결정이 건강한 미래로 이끌어줄 것입니다. 용기를 내어 첫 발을 내딛은 당신이 자랑스럽습니다").build());
			ChallengeLevel challengeLevel2 = challengeLevelRepository.save(ChallengeLevel.builder().levelNumber(2L).reward("레벨2 보상").duration(1140480000L).image(currentDomain + "/image/levels/level2.png").title("레벨 2: 초기 도전").message("이제 금연 여정이 시작됐습니다. 매일 금연을 유지하는 것이 쉽지 않을 수 있지만, 당신은 이미 중요한 발걸음을 내디뎠어요. 작은 성공들을 쌓아가며 계속 전진해보세요!").build());
			ChallengeLevel challengeLevel3 = challengeLevelRepository.save(ChallengeLevel.builder().levelNumber(3L).reward("레벨3 보상").duration(1710720000L).image(currentDomain + "/image/levels/level3.png").title("레벨 3: 습관 형성").message("금연이 조금씩 습관이 되어가고 있어요. 이제 당신의 새로운 일상이 형성되고 있습니다. 매일의 작은 승리를 기념하며, 금연을 통한 변화를 느껴보세요.").build());
			ChallengeLevel challengeLevel4 = challengeLevelRepository.save(ChallengeLevel.builder().levelNumber(4L).reward("레벨4 보상").duration(2280960000L).image(currentDomain + "/image/levels/level4.png").title("레벨 4: 일상 속에서").message("금연이 일상의 일부가 되어가고 있습니다. 힘든 순간도 있겠지만, 지금까지 이룬 성과에 자부심을 가져도 좋아요. 당신의 건강한 선택이 빛을 발하고 있습니다!").build());
			ChallengeLevel challengeLevel5 = challengeLevelRepository.save(ChallengeLevel.builder().levelNumber(5L).reward("레벨5 보상").duration(2851200000L).image(currentDomain + "/image/levels/level5.png").title("레벨 5: 중간 지점").message("이제 금연 여정의 절반을 넘어섰어요! 매일 꾸준히 금연을 유지하는 것이 얼마나 대단한지 잊지 마세요. 당신의 의지와 노력이 멋진 결과로 이어지고 있습니다.").build());
			ChallengeLevel challengeLevel6 = challengeLevelRepository.save(ChallengeLevel.builder().levelNumber(6L).reward("레벨6 보상").duration(3421440000L).image(currentDomain + "/image/levels/level6.png").title("레벨 6: 승리의 맛").message("금연으로 인해 당신의 삶에 긍정적인 변화가 생기고 있어요. 더 맑은 호흡, 개선된 건강 상태를 느껴보세요. 이 모든 변화는 당신의 노력 덕분입니다!").build());
			ChallengeLevel challengeLevel7 = challengeLevelRepository.save(ChallengeLevel.builder().levelNumber(7L).reward("레벨7 보상").duration(3991680000L).image(currentDomain + "/image/levels/level7.png").title("레벨 7: 건강한 습관").message("금연은 이제 당신의 건강한 습관이 되었습니다. 지금까지의 여정을 되돌아보며 자신감을 갖고 앞으로 나아가세요. 당신의 결정이 삶을 얼마나 변화시켰는지 기억하세요.").build());
			ChallengeLevel challengeLevel8 = challengeLevelRepository.save(ChallengeLevel.builder().levelNumber(8L).reward("레벨8 보상").duration(4561920000L).image(currentDomain + "/image/levels/level8.png").title("레벨 8: 자신감 증진").message("이제 당신은 금연을 통해 높은 자신감을 얻었습니다. 어려운 도전을 극복하고 이 자리에 있기까지, 당신은 놀라운 일을 해냈습니다. 자신감을 가지고 계속 전진하세요!").build());
			ChallengeLevel challengeLevel9 = challengeLevelRepository.save(ChallengeLevel.builder().levelNumber(9L).reward("레벨9 보상").duration(5132160000L).image(currentDomain + "/image/levels/level9.png").title("레벨 9: 결실의 시기").message("금연 여정의 끝이 보이기 시작합니다. 오랜 시간 동안의 노력이 이제 결실을 맺고 있어요. 당신의 건강한 삶을 위한 선택이 얼마나 가치 있는지 잊지 마세요.").build());
			ChallengeLevel challengeLevel10 = challengeLevelRepository.save(ChallengeLevel.builder().levelNumber(10L).reward("레벨10 보상").duration(3155760000000L).image(currentDomain + "/image/levels/level10.png").title("레벨 10: 완전한 성공").message("축하합니다! 금연의 최종 단계에 도달했습니다. 이는 단순한 마무리가 아니라, 새로운 건강한 생활의 시작입니다. 당신의 끈기와 의지가 이뤄낸 놀라운 성과에 경의를 표합니다. 이제 당신은 자신의 건강과 행복을 위해 최선을 다할 수 있는 힘을 갖추었습니다. 금연 여정을 완료한 당신을 진심으로 축하드리며, 앞으로 더욱 건강하고 활기찬 삶을 이어가시길 바랍니다. 당신의 성공이 다른 이들에게도 영감을 주는 소중한 이정표가 되길 희망합니다. 정말 대단하십니다!").build());

			log.info("=================================================================================");*/

			//6.6일 단위
			log.info("========================= Challenge Level Insert ================================");
			ChallengeLevel challengeLevel0 = challengeLevelRepository.save(ChallengeLevel.builder().levelNumber(0L).reward("레벨0").duration(0L).image("").build()); //0분
			ChallengeLevel challengeLevel1 = challengeLevelRepository.save(ChallengeLevel.builder().levelNumber(1L).reward("레벨1 보상").duration(570240000L).image(currentDomain + "/image/levels/level1.png").title("레벨 1: 시작 단계").message("새로운 시작을 축하합니다! 금연의 첫걸음을 내딛는 것이 가장 중요합니다. 오늘 당신이 내린 결정이 건강한 미래로 이끌어줄 것입니다. 용기를 내어 첫 발을 내딛은 당신이 자랑스럽습니다").build());
			ChallengeLevel challengeLevel2 = challengeLevelRepository.save(ChallengeLevel.builder().levelNumber(2L).reward("레벨2 보상").duration(1140480000L).image(currentDomain + "/image/levels/level2.png").title("레벨 2: 초기 도전").message("이제 금연 여정이 시작됐습니다. 매일 금연을 유지하는 것이 쉽지 않을 수 있지만, 당신은 이미 중요한 발걸음을 내디뎠어요. 작은 성공들을 쌓아가며 계속 전진해보세요!").build());
			ChallengeLevel challengeLevel3 = challengeLevelRepository.save(ChallengeLevel.builder().levelNumber(3L).reward("레벨3 보상").duration(1710720000L).image(currentDomain + "/image/levels/level3.png").title("레벨 3: 습관 형성").message("금연이 조금씩 습관이 되어가고 있어요. 이제 당신의 새로운 일상이 형성되고 있습니다. 매일의 작은 승리를 기념하며, 금연을 통한 변화를 느껴보세요.").build());
			ChallengeLevel challengeLevel4 = challengeLevelRepository.save(ChallengeLevel.builder().levelNumber(4L).reward("레벨4 보상").duration(2280960000L).image(currentDomain + "/image/levels/level4.png").title("레벨 4: 일상 속에서").message("금연이 일상의 일부가 되어가고 있습니다. 힘든 순간도 있겠지만, 지금까지 이룬 성과에 자부심을 가져도 좋아요. 당신의 건강한 선택이 빛을 발하고 있습니다!").build());
			ChallengeLevel challengeLevel5 = challengeLevelRepository.save(ChallengeLevel.builder().levelNumber(5L).reward("레벨5 보상").duration(2851200000L).image(currentDomain + "/image/levels/level5.png").title("레벨 5: 중간 지점").message("이제 금연 여정의 절반을 넘어섰어요! 매일 꾸준히 금연을 유지하는 것이 얼마나 대단한지 잊지 마세요. 당신의 의지와 노력이 멋진 결과로 이어지고 있습니다.").build());
			ChallengeLevel challengeLevel6 = challengeLevelRepository.save(ChallengeLevel.builder().levelNumber(6L).reward("레벨6 보상").duration(3421440000L).image(currentDomain + "/image/levels/level6.png").title("레벨 6: 승리의 맛").message("금연으로 인해 당신의 삶에 긍정적인 변화가 생기고 있어요. 더 맑은 호흡, 개선된 건강 상태를 느껴보세요. 이 모든 변화는 당신의 노력 덕분입니다!").build());
			ChallengeLevel challengeLevel7 = challengeLevelRepository.save(ChallengeLevel.builder().levelNumber(7L).reward("레벨7 보상").duration(3991680000L).image(currentDomain + "/image/levels/level7.png").title("레벨 7: 건강한 습관").message("금연은 이제 당신의 건강한 습관이 되었습니다. 지금까지의 여정을 되돌아보며 자신감을 갖고 앞으로 나아가세요. 당신의 결정이 삶을 얼마나 변화시켰는지 기억하세요.").build());
			ChallengeLevel challengeLevel8 = challengeLevelRepository.save(ChallengeLevel.builder().levelNumber(8L).reward("레벨8 보상").duration(4561920000L).image(currentDomain + "/image/levels/level8.png").title("레벨 8: 자신감 증진").message("이제 당신은 금연을 통해 높은 자신감을 얻었습니다. 어려운 도전을 극복하고 이 자리에 있기까지, 당신은 놀라운 일을 해냈습니다. 자신감을 가지고 계속 전진하세요!").build());
			ChallengeLevel challengeLevel9 = challengeLevelRepository.save(ChallengeLevel.builder().levelNumber(9L).reward("레벨9 보상").duration(5132160000L).image(currentDomain + "/image/levels/level9.png").title("레벨 9: 결실의 시기").message("금연 여정의 끝이 보이기 시작합니다. 오랜 시간 동안의 노력이 이제 결실을 맺고 있어요. 당신의 건강한 삶을 위한 선택이 얼마나 가치 있는지 잊지 마세요.").build());
			ChallengeLevel challengeLevel10 = challengeLevelRepository.save(ChallengeLevel.builder().levelNumber(10L).reward("레벨10 보상").duration(3155760000000L).image(currentDomain + "/image/levels/level10.png").title("레벨 10: 완전한 성공").message("축하합니다! 금연의 최종 단계에 도달했습니다. 이는 단순한 마무리가 아니라, 새로운 건강한 생활의 시작입니다. 당신의 끈기와 의지가 이뤄낸 놀라운 성과에 경의를 표합니다. 이제 당신은 자신의 건강과 행복을 위해 최선을 다할 수 있는 힘을 갖추었습니다. 금연 여정을 완료한 당신을 진심으로 축하드리며, 앞으로 더욱 건강하고 활기찬 삶을 이어가시길 바랍니다. 당신의 성공이 다른 이들에게도 영감을 주는 소중한 이정표가 되길 희망합니다. 정말 대단하십니다!").build());

			log.info("=================================================================================");


		};
	}



	@Bean
	@Profile("prod")
	public CommandLineRunner prodInit(ChallengeLevelRepository challengeLevelRepository) {
		return (args) -> {


			//6.6일 단위
			log.info("========================= Challenge Level Insert ================================");

			ChallengeLevel challengeLevel0 = challengeLevelRepository.findByLevelNumber(0L);
			if (challengeLevel0 == null) {
				challengeLevelRepository.save(ChallengeLevel.builder().levelNumber(0L).reward("레벨0").duration(0L).image("").build()); //0분
			}

			ChallengeLevel challengeLevel1 = challengeLevelRepository.findByLevelNumber(1L);
			if (challengeLevel1 == null) {
				challengeLevelRepository.save(ChallengeLevel.builder().levelNumber(1L).reward("레벨1 보상").duration(10000L).image(currentDomain + "/image/levels/level1.png").title("레벨 1: 시작 단계").message("새로운 시작을 축하합니다! 금연의 첫걸음을 내딛는 것이 가장 중요합니다. 오늘 당신이 내린 결정이 건강한 미래로 이끌어줄 것입니다. 용기를 내어 첫 발을 내딛은 당신이 자랑스럽습니다").build());
			}

			ChallengeLevel challengeLevel2 = challengeLevelRepository.findByLevelNumber(2L);
			if (challengeLevel2 == null) {
				challengeLevelRepository.save(ChallengeLevel.builder().levelNumber(2L).reward("레벨2 보상").duration(20000L).image(currentDomain + "/image/levels/level2.png").title("레벨 2: 초기 도전").message("이제 금연 여정이 시작됐습니다. 매일 금연을 유지하는 것이 쉽지 않을 수 있지만, 당신은 이미 중요한 발걸음을 내디뎠어요. 작은 성공들을 쌓아가며 계속 전진해보세요!").build());
			}

			ChallengeLevel challengeLevel3 = challengeLevelRepository.findByLevelNumber(3L);
			if (challengeLevel3 == null) {
				challengeLevelRepository.save(ChallengeLevel.builder().levelNumber(3L).reward("레벨3 보상").duration(30000L).image(currentDomain + "/image/levels/level3.png").title("레벨 3: 습관 형성").message("금연이 조금씩 습관이 되어가고 있어요. 이제 당신의 새로운 일상이 형성되고 있습니다. 매일의 작은 승리를 기념하며, 금연을 통한 변화를 느껴보세요.").build());
			}

			ChallengeLevel challengeLevel4 = challengeLevelRepository.findByLevelNumber(4L);
			if (challengeLevel4 == null) {
				challengeLevelRepository.save(ChallengeLevel.builder().levelNumber(4L).reward("레벨4 보상").duration(40000L).image(currentDomain + "/image/levels/level4.png").title("레벨 4: 일상 속에서").message("금연이 일상의 일부가 되어가고 있습니다. 힘든 순간도 있겠지만, 지금까지 이룬 성과에 자부심을 가져도 좋아요. 당신의 건강한 선택이 빛을 발하고 있습니다!").build());
			}


			ChallengeLevel challengeLevel5 = challengeLevelRepository.findByLevelNumber(5L);
			if (challengeLevel5 == null) {
				challengeLevelRepository.save(ChallengeLevel.builder().levelNumber(5L).reward("레벨5 보상").duration(50000L).image(currentDomain + "/image/levels/level5.png").title("레벨 5: 중간 지점").message("이제 금연 여정의 절반을 넘어섰어요! 매일 꾸준히 금연을 유지하는 것이 얼마나 대단한지 잊지 마세요. 당신의 의지와 노력이 멋진 결과로 이어지고 있습니다.").build());
			}

			ChallengeLevel challengeLevel6 = challengeLevelRepository.findByLevelNumber(6L);
			if (challengeLevel6 == null) {
				challengeLevelRepository.save(ChallengeLevel.builder().levelNumber(6L).reward("레벨6 보상").duration(60000L).image(currentDomain + "/image/levels/level6.png").title("레벨 6: 승리의 맛").message("금연으로 인해 당신의 삶에 긍정적인 변화가 생기고 있어요. 더 맑은 호흡, 개선된 건강 상태를 느껴보세요. 이 모든 변화는 당신의 노력 덕분입니다!").build());

			}

			ChallengeLevel challengeLevel7 = challengeLevelRepository.findByLevelNumber(7L);
			if (challengeLevel7 == null) {
				challengeLevelRepository.save(ChallengeLevel.builder().levelNumber(7L).reward("레벨7 보상").duration(70000L).image(currentDomain + "/image/levels/level7.png").title("레벨 7: 건강한 습관").message("금연은 이제 당신의 건강한 습관이 되었습니다. 지금까지의 여정을 되돌아보며 자신감을 갖고 앞으로 나아가세요. 당신의 결정이 삶을 얼마나 변화시켰는지 기억하세요.").build());

			}

			ChallengeLevel challengeLevel8 = challengeLevelRepository.findByLevelNumber(8L);
			if (challengeLevel8 == null) {
				challengeLevelRepository.save(ChallengeLevel.builder().levelNumber(8L).reward("레벨8 보상").duration(80000L).image(currentDomain + "/image/levels/level8.png").title("레벨 8: 자신감 증진").message("이제 당신은 금연을 통해 높은 자신감을 얻었습니다. 어려운 도전을 극복하고 이 자리에 있기까지, 당신은 놀라운 일을 해냈습니다. 자신감을 가지고 계속 전진하세요!").build());

			}

			ChallengeLevel challengeLevel9 = challengeLevelRepository.findByLevelNumber(9L);
			if (challengeLevel9 == null) {
				challengeLevelRepository.save(ChallengeLevel.builder().levelNumber(9L).reward("레벨9 보상").duration(90000L).image(currentDomain + "/image/levels/level9.png").title("레벨 9: 결실의 시기").message("금연 여정의 끝이 보이기 시작합니다. 오랜 시간 동안의 노력이 이제 결실을 맺고 있어요. 당신의 건강한 삶을 위한 선택이 얼마나 가치 있는지 잊지 마세요.").build());

			}

			ChallengeLevel challengeLevel10 = challengeLevelRepository.findByLevelNumber(10L);
			if (challengeLevel10 == null) {
				challengeLevelRepository.save(ChallengeLevel.builder().levelNumber(10L).reward("레벨10 보상").duration(100000000L).image(currentDomain + "/image/levels/level10.png").title("레벨 10: 완전한 성공").message("축하합니다! 금연의 최종 단계에 도달했습니다. 이는 단순한 마무리가 아니라, 새로운 건강한 생활의 시작입니다. 당신의 끈기와 의지가 이뤄낸 놀라운 성과에 경의를 표합니다. 이제 당신은 자신의 건강과 행복을 위해 최선을 다할 수 있는 힘을 갖추었습니다. 금연 여정을 완료한 당신을 진심으로 축하드리며, 앞으로 더욱 건강하고 활기찬 삶을 이어가시길 바랍니다. 당신의 성공이 다른 이들에게도 영감을 주는 소중한 이정표가 되길 희망합니다. 정말 대단하십니다!").build());
			}


			log.info("=================================================================================");







		};
	}

}
